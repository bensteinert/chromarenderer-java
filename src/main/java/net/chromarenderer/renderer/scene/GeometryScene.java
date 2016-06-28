package net.chromarenderer.renderer.scene;

import net.chromarenderer.main.ChromaStatistics;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.scene.acc.AccStructType;
import net.chromarenderer.renderer.scene.acc.AccelerationStructure;
import net.chromarenderer.renderer.scene.acc.BvhStrategyType;
import net.chromarenderer.renderer.scene.acc.BvhTreeBuilder;
import net.chromarenderer.renderer.scene.acc.IntersectionContext;
import net.chromarenderer.renderer.scene.acc.NoAccelerationImpl;
import net.chromarenderer.renderer.shader.MaterialType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author bensteinert
 */
public class GeometryScene implements ChromaScene {

    private final List<Geometry> geometryList;
    private final List<Geometry> lightSources;
    private final float[] lightSourceDistributions;
    private final float totalLightSourceArea;

    private Camera camera;
    private AccelerationStructure accStruct;


    public GeometryScene(List<Geometry> geometryList) {
        this.geometryList = geometryList;
        lightSources = filterEmittingGeometry(geometryList);
        accStruct = new NoAccelerationImpl(geometryList);

        if (lightSources.size() > 0) {
            lightSourceDistributions = new float[lightSources.size()];

            float totalArea = 0.0f;
            for (Geometry lightSource : lightSources) {
                totalArea += lightSource.getArea();
            }

            this.totalLightSourceArea = totalArea;

            // bootstrap
            lightSourceDistributions[0] = lightSources.get(0).getArea() / totalArea;

            for (int i = 1; i < lightSources.size(); i++) {
                lightSourceDistributions[i] = lightSourceDistributions[i - 1] + lightSources.get(i).getArea() / totalArea;
            }
        } else {
            totalLightSourceArea = 0.0f;
            lightSourceDistributions = null;
            System.err.println("No LightSources detected");
            // TODO throw exception
        }
    }


    private static List<Geometry> filterEmittingGeometry(List<Geometry> geometryList) {
        List<Geometry> result = new ArrayList<>();
        geometryList.forEach(elem -> {
            if (MaterialType.EMITTING.equals(elem.getMaterial().getType())) {
                result.add(elem);
            }
        });
        return Collections.unmodifiableList(result);
    }


    private static final ThreadLocal<IntersectionContext> intersectionContextHolder = ThreadLocal.withInitial(IntersectionContext::new);


    public Hitpoint intersect(Ray ray) {
        ChromaStatistics.ray();
        //TODO-IMP: Measure overhead of ThreadLocal
        IntersectionContext intersectionContext = intersectionContextHolder.get();
        intersectionContext.reinit(ray);

        accStruct.intersect(intersectionContext);

        if (intersectionContext.hitGeometry != null) {
            ImmutableVector3 hitpoint = ray.onRay(intersectionContext.hitDistance);
            //hitpoint = increaseHitpointPrecision(ray, intersectionContext.hitGeometry, hitpoint, intersectionContext.hitDistance);
            ImmutableVector3 hitpointNormal = intersectionContext.hitGeometry.getNormal(hitpoint);
            //hitpoint = hitpoint.plus(hitpointNormal.mult(Constants.FLT_EPSILON));
            ray.mailbox(intersectionContext.hitGeometry);
            return new Hitpoint(intersectionContext.hitGeometry, intersectionContext.hitDistance, hitpoint, hitpointNormal);
        } else {
            return Hitpoint.INFINITY;
        }
    }

    public boolean isObstructed(Ray ray) {
        ChromaStatistics.ray();
        IntersectionContext intersectionContext = intersectionContextHolder.get();
        intersectionContext.reinit(ray, IntersectionContext.ANY);
        accStruct.intersect(intersectionContext);
        return intersectionContext.hitGeometry != null;
    }


    /**
     * When ray suffered distance precision loss it is good to do a 2nd intersection in order to correct the actual hitpoint.
     */
    private ImmutableVector3 increaseHitpointPrecision(Ray ray, Geometry hitGeometry, ImmutableVector3 hitpoint, float hitDistance) {
        Ray reverseRay = new Ray(hitpoint, ray.getDirection().mult(-1.0f), 0.0f, hitDistance * 0.5f);
        float reverseDistance = hitGeometry.intersect(reverseRay);
        if (reverseDistance > 0) {
            ChromaStatistics.subsurfaceHitpointCorrected();
            return reverseRay.onRay(reverseDistance);
        } else {
            return hitpoint;
        }
    }


    public Hitpoint getLightSourceSample() {
        float random = ChromaThreadContext.randomFloatClosedOpen();

        int lightSourceIdx;
        lightSourceIdx = 0;
        while (lightSourceIdx < lightSources.size() && random > lightSourceDistributions[lightSourceIdx]) {
            lightSourceIdx++;
        }

        Geometry sampledGeometry = lightSources.get(lightSourceIdx);
        ImmutableVector3 surfaceSample = sampledGeometry.getUnifDistrSample();

        return new Hitpoint(sampledGeometry, surfaceSample, sampledGeometry.getNormal(surfaceSample), totalLightSourceArea);
    }


    public void buildAccelerationStructure(AccStructType type){
        // nothing to do, scene is static, result will be the same
        if (Objects.equals(type, accStruct.getType())){
            return;
        }
        switch (type) {
            case AABB_BVH:
                BvhTreeBuilder treeBuilder = new BvhTreeBuilder(4, 20);
                accStruct = treeBuilder.buildBvh(geometryList, BvhStrategyType.TOP_DOWN);
                break;
            case LIST:
            default:
                accStruct = new NoAccelerationImpl(geometryList);
                break;
        }
    }


    public Camera getCamera() {
        return camera;
    }


    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
