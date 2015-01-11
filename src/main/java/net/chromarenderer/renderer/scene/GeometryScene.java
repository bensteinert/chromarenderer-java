package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.COLORS;
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


    public Radiance getRadianceSample(Hitpoint hitpoint) {
        ImmutableVector3 point = hitpoint.getPoint();
        ImmutableVector3 direction = pointLight.subtract(point);

        float distToLightSource = direction.length();
        Ray shadowRay = new Ray(point, direction.normalize(), 0.0f, distToLightSource);

        float cosThetaSceneHit = direction.dot(hitpoint.getHitpointNormal());

        //geometry hit from correct side?
        if (cosThetaSceneHit > 0.0f) {
            for (Geometry shadowGeometry : geometryList) {
                float distance = shadowGeometry.intersect(shadowRay);
                if (shadowRay.isOnRay(distance)) {
                    new Radiance(COLORS.BLACK, shadowRay);
                }
            }
        }
        else {
            return new Radiance(COLORS.BLACK, shadowRay);
        }

        float geomTerm =  (cosThetaSceneHit) / (distToLightSource*distToLightSource);
        Vector3 rhoDiffuse = hitpoint.getHitGeometry().getColor();
        float precisionBound = 10.0f/(rhoDiffuse.getMaxValue()); 														// bound can include brdf which can soften the geometric term
        Vector3 result = rhoDiffuse.div(Constants.PI_f).mult(Math.min(precisionBound, geomTerm));

        return new Radiance(result.mult(2.0f), shadowRay);
    }


}
