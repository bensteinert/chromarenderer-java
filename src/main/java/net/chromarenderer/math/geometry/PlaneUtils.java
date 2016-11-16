package net.chromarenderer.math.geometry;

import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;

/**
 * @author bensteinert
 */
public class PlaneUtils {

    public static float planeRayIntersection(Vector3 center, Vector3 normal, Ray ray) {
        float OminusPmultN = ray.getOrigin().minus(center).dot(normal);
        float DmultN = ray.getDirection().dot(normal);
        if (DmultN != 0.0f) {
            //ray and plane are not parallel
            float t = OminusPmultN / DmultN;
            return -t;
        }
        // intersection just from normal-facing side
        return 0.0f;
    }
}
