package net.chromarenderer.renderer.scene;

import net.chromarenderer.main.ChromaStatistics;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Sphere;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.shader.Material;

/**
 * @author bensteinert
 */
public class FurnaceTest implements ChromaScene {

    Sphere innerSphere = new Sphere(Vector3.ORIGIN, 1.0f, Material.createDiffuseMaterial(COLORS.GREY));
    Sphere outerSphere = new Sphere(Vector3.ORIGIN, 100000.0f, Material.createEmittingMaterial(COLORS.FULL_WHITE, 1.0f));


    @Override
    public Hitpoint intersect(Ray ray) {
        if (ray.getLastHitGeomerty() != innerSphere) {
            float distance = innerSphere.intersect(ray);
            if (ray.isOnRay(distance)) {
                final ImmutableVector3 point = ray.onRay(distance);
                ray.mailbox(innerSphere);
                return new Hitpoint(innerSphere, point, innerSphere.getNormal(point));
            }
        } else {
            ChromaStatistics.subsurfaceHitpointCorrected();
        }

        float envHit = outerSphere.intersect(ray);
        final ImmutableVector3 point = ray.onRay(envHit);
        return new Hitpoint(outerSphere, point, outerSphere.getNormal(point).mult(-1.0f));
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
}
