package net.chroma.renderer.scene;

import net.chroma.math.COLORS;
import net.chroma.math.Constants;
import net.chroma.math.ImmutableVector3;
import net.chroma.math.Vector3;
import net.chroma.math.geometry.Geometry;
import net.chroma.math.raytracing.Hitpoint;
import net.chroma.math.raytracing.Ray;

import java.util.List;

/**
 * @author steinerb
 */
public class GeometryScene {

    private final List<Geometry> geometryList;
    private final ImmutableVector3 pointLight = new ImmutableVector3(0.0f, 1.5f, 0.0f);


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
        }

        else return Hitpoint.INFINITY;

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


    public Vector3 enlighten(Hitpoint hitpoint) {
        ImmutableVector3 point = hitpoint.getPoint();
        ImmutableVector3 direction = pointLight.subtract(point);

        // currently light is always WHITE
        Vector3 resultLight = COLORS.WHITE;

        float distToLightSource = direction.length();
        Ray shadowRay = new Ray(point, direction.normalize(), 0.0f, distToLightSource);

        for (Geometry shadowGeometry : geometryList) {
            float distance = shadowGeometry.intersect(shadowRay);
            if (shadowRay.isOnRay(distance)) {
                resultLight = COLORS.BLACK;
                break;
            }
        }

        return resultLight;
    }
}
