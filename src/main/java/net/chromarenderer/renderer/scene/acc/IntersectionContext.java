package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.raytracing.Ray;

public class IntersectionContext {
    public Geometry hitGeometry;
    public float hitDistance;
    public Ray ray;

    public void reinit(){
        hitDistance = Float.MAX_VALUE;
        hitGeometry = null;
    }

    public void reinit(Ray ray){
        hitDistance = ray.getTMax();
        hitGeometry = null;
        this.ray = ray;
    }

    public void checkGeometry(Geometry geometry) {
        float distance = geometry.intersect(ray);
        if (ray.isOnRay(distance) && distance < hitDistance) {
            hitGeometry = geometry;
            hitDistance = distance;
        }
    }
}