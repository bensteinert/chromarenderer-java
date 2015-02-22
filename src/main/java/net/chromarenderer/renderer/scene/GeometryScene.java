package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.geometry.PhotonFountain;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.math.shader.MaterialType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author steinerb
 */
public class GeometryScene {

    public final List<Geometry> geometryList;
    public final PhotonFountain pointLight = new PhotonFountain(new ImmutableVector3(0.0f, 1.99f, 0.0f), 1.0f);

    public final List<Geometry> lightSources;
    public final float[] lightSourceDistributions;
    public final float totalLightSourceArea;


    public GeometryScene(List<Geometry> geometryList) {
        this.geometryList = geometryList;
        lightSources = filterEmittingGeometry(geometryList);

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


    public Hitpoint intersect(Ray ray) {
        float hitDistance = Float.MAX_VALUE;
        Geometry hitGeometry = null;
        ImmutableVector3 hitpoint = null;
        for (Geometry geometry : geometryList) {
            float distance = geometry.intersect(ray);
            if (ray.isOnRay(distance) && distance < hitDistance) {
                hitGeometry = geometry;
                hitDistance = distance;
            }
        }

        if (hitGeometry != null) {
            hitpoint = ray.onRay(hitDistance);
            hitpoint = increaseHitpointPrecision(ray, hitGeometry, hitpoint, hitDistance);
            ImmutableVector3 hitpointNormal = hitGeometry.getNormal(hitpoint);
            hitpoint = hitpoint.plus(hitpointNormal.mult(Constants.FLT_EPSILON));
            return new Hitpoint(hitGeometry, hitpoint, hitpointNormal);
        } else {
            return Hitpoint.INFINITY;
        }
    }


    private ImmutableVector3 increaseHitpointPrecision(Ray ray, Geometry hitGeometry, ImmutableVector3 hitpoint, float hitDistance) {
        Ray reverseRay = new Ray(hitpoint, ray.getDirection().mult(-1.0f), 0.0f, hitDistance - Constants.FLT_EPSILON);
        float reverseDistance = hitGeometry.intersect(reverseRay);
        if (reverseDistance > 0) {
            return reverseRay.onRay(reverseDistance);
        } else {
            return hitpoint;
        }
    }


    public Hitpoint getLightSourceSample() {
//        float random = ChromaThreadContext.randomFloatClosedOpen();
//
//        int lightSourceIdx;
//        for (lightSourceIdx = 0; lightSourceIdx < lightSources.size() && random > lightSourceDistributions[lightSourceIdx]; lightSourceIdx++)
//            ;
//
//        Geometry sampledGeometry = lightSources.get(lightSourceIdx);
//        ImmutableVector3 surfaceSample = sampledGeometry.getUnifDistrSample();

//        return new Hitpoint(sampledGeometry, surfaceSample, sampledGeometry.getNormal(surfaceSample), totalLightSourceArea);

        return new Hitpoint(pointLight, pointLight.getUnifDistrSample(), null, 1.0f);
    }

}
