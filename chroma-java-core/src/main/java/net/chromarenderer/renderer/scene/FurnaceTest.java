package net.chromarenderer.renderer.scene;

import net.chromarenderer.utils.ChromaStatistics;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.geometry.Sphere;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.camera.PinholeCamera;
import net.chromarenderer.renderer.shader.Material;

/**
 * @author bensteinert
 */
public class FurnaceTest implements ChromaScene {

    private final Sphere innerSphere;
    private final Sphere outerSphere;
    private final PinholeCamera camera;

    private FurnaceTest(Sphere innerSphere, Sphere outerSphere, PinholeCamera camera) {
        this.innerSphere = innerSphere;
        this.outerSphere = outerSphere;
        this.camera = camera;
    }


    public static ChromaScene create() {
        PinholeCamera camera = PinholeCamera.createWithDefaults();
        Sphere innerSphere = new Sphere(new ImmutableVector3(1.0f, 1.0f, 1.0f), 1.0f, Material.createDiffuseMaterial(COLORS.GREY));
        Sphere outerSphere = new Sphere(new ImmutableVector3(1.0f, 1.0f, 1.0f), 100000.0f, Material.createEmittingMaterial(COLORS.FULL_WHITE, 1.0f));
        return new FurnaceTest(innerSphere, outerSphere, camera);
    }


    @Override
    public Hitpoint intersect(Ray ray) {
        if (ray.getLastHitGeomerty() != innerSphere) {
            float distance = innerSphere.intersect(ray);
            if (ray.isOnRay(distance)) {
                final ImmutableVector3 point = ray.onRay(distance);
                ray.mailbox(innerSphere);
                return new Hitpoint(innerSphere, distance, point, innerSphere.getNormal(point));
            }
        } else {
            ChromaStatistics.subsurfaceHitpointCorrected();
        }

        float envHit = outerSphere.intersect(ray);
        final ImmutableVector3 point = ray.onRay(envHit);
        return new Hitpoint(outerSphere, Float.MAX_VALUE, point, outerSphere.getNormal(point).mult(-1.0f));
    }


    @Override
    public Hitpoint getLightSourceSample() {
        final ImmutableVector3 sample = outerSphere.getUnifDistrSample();
        return new Hitpoint(outerSphere, sample, outerSphere.getNormal(sample).mult(-1.0f), outerSphere.getArea());
    }


    @Override
    public boolean isObstructed(Ray shadowRay) {
        return false;
    }


    @Override
    public int getNumberOfLightSources() {
        return 1;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }
}
