package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;

import java.util.List;

/**
 * @author steinerb
 */
public class GeometryScene {

    public final List<Geometry> geometryList;
    public final ImmutableVector3 pointLight = new ImmutableVector3(0.0f, 1.5f, 0.0f);


    public GeometryScene(List<Geometry> geometryList) {
        this.geometryList = geometryList;
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
            hitpoint = increaseHitpointPrecision(ray, hitGeometry, hitpoint);
            Vector3 hitpointNormal = hitGeometry.getNormal(hitpoint);
            hitpoint = hitpoint.plus(hitpointNormal.mult(Constants.FLT_EPSILON));
            return new Hitpoint(hitGeometry, hitpoint, hitDistance, hitpointNormal);
        } else return Hitpoint.INFINITY;

    }


    private ImmutableVector3 increaseHitpointPrecision(Ray cameraRay, Geometry hitGeometry, ImmutableVector3 hitpoint) {
        Ray reverseRay = new Ray(hitpoint, cameraRay.getDirection().mult(-1.0f));
        float reverseDistance = hitGeometry.intersect(reverseRay);
        if (reverseDistance > 0) {
            return reverseRay.onRay(reverseDistance);
        } else {
//            statistics.reverseRayMissed();
        }
        return hitpoint;
    }

}
