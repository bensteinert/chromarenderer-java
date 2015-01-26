package net.chromarenderer.math.geometry;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableArrayMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.math.shader.Material;

/**
 * @author steinerb
 */
public class Sphere extends AbstractGeometry {

    private final Vector3 center;
    private final double radius;

    public Sphere(Vector3 center, double radius, Material material) {
        super(material);
        this.center = center;
        this.radius = radius;
    }

    /**
     * Adapted from <a href="http://wiki.cgsociety.org/index.php/Ray_Sphere_Intersection">http://wiki.cgsociety.org/index.php/Ray_Sphere_Intersection</a>
     * @param ray to intersect with
     * @return distance to first hit.
     */
    @Override
    public float intersect(Ray ray) {
        ImmutableVector3 OminusC = ray.getOrigin().minus(center);

        double a = ray.getDirection().dot(ray.getDirection());
        double b = ray.getDirection().dot(OminusC) * 2.0;
        double c = OminusC.dot(OminusC) - radius * radius;

        //Find discriminant
        double disc = (b * b) - (4.0 * a * c);

        // if discriminant is negative there are no real roots, so return
        // false as ray misses sphere
        if (disc < 0.0) return 0.0f;

        // compute q as described above
        double distSqrt = Math.sqrt(disc);
        double q = b<0 ? (-b - distSqrt)*0.5 : (-b + distSqrt)*0.5;

        // compute t0 and t1
        double t0 = q / a;
        double t1 = c / q;

        // make sure t0 is smaller than t1
        if (t0 > t1) {
            double temp = t0;
            t0 = t1;
            t1 = temp;
        }

        // ignore imprecision flaws.....
        if(!(t0 < Constants.SPHERE_NAN_LIMIT) || !(t1 < Constants.SPHERE_NAN_LIMIT)){
            return 0.0f;
        }

        // if t1 is less than tmin, the object is in the ray's negative direction
        // and consequently the ray misses the sphere
        if (t1 < 0) {
            return 0.0f;
        }

        // if t0 is less than zero, the intersection point is at t1
        if (t0 < 0) {
            return (float) t1;
            // else the intersection point is at t0
        } else {
            return (float) t0;
        }
    }

    @Override
    public Geometry transpose(Vector3 transpose) {
        return null;
    }

    @Override
    public Geometry rotate(ImmutableArrayMatrix3x3 rotationY) {
        return null;
    }

    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        return hitpoint.minus(center).normalize();
    }

    @Override
    public final boolean isPlane() {
        return false;
    }
}
