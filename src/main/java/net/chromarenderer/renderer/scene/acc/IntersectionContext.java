package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.raytracing.Ray;

public class IntersectionContext {

    public static final int ANY = 4;
    public static final int FIRST = 0;

    public Geometry hitGeometry;
    public float hitDistance;
    public Ray ray;
    float[] lastTValues = {0.0f, 0.0f};
    int intersectionMode = FIRST;


    public void reinit() {
        hitDistance = Float.MAX_VALUE;
        hitGeometry = null;
        intersectionMode = FIRST;
    }


    public void reinit(Ray ray) {
        hitDistance = ray.getTMax();
        hitGeometry = null;
        this.ray = ray;
        intersectionMode = FIRST;
    }


    public void checkGeometry(Geometry geometry) {
        if (geometry != ray.getLastHitGeomerty()) {
            float distance = geometry.intersect(ray);
            if (ray.isOnRay(distance) && distance < hitDistance && distance > Constants.FLT_EPSILON) {
                hitGeometry = geometry;
                hitDistance = distance;
            }
        }
    }


    public void reinit(Ray ray, int mode) {
        reinit(ray);
        intersectionMode = mode;
    }
}