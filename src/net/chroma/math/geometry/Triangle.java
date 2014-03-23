package net.chroma.math.geometry;

import net.chroma.math.Constants;
import net.chroma.math.ImmutableArrayMatrix3x3;
import net.chroma.math.ImmutableVector3;
import net.chroma.math.Vector3;
import net.chroma.math.raytracing.Ray;

/**
 * Chroma uses the right-hand-coordinate system. Think about three vertices specified counterclockwise on the floor.
 * The normal will always point upwards!
 * @author steinerb
 */
public class Triangle implements Geometry {

    private static final double EPSILON = 0.000001;

    private final ImmutableVector3 p0, p1, p2;
    private final ImmutableVector3 n;

    public Triangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n = n;

    }

    public Triangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n = e1().crossProduct(e2()).normalize();
    }

    @Override
    public float intersect(Ray ray){

        ImmutableVector3 P, Q, T;
        float det;

        ImmutableVector3 E1 = e1();
        ImmutableVector3 E2 = e2();

        /* begin calculating determinant - also used to calculate U parameter */
        P = ray.getDirection().crossProduct(E2);

        det = E1.dot(P);

        /* if determinant is near zero, ray lies in plane of triangle */
        if(det > -Constants.DBL_EPSILON && det < Constants.DBL_EPSILON){
            return 0.f;
        }

        // backface culling?
//      #ifdef BACKFACECULLING
//    /* calculate distance from vert0 to ray origin */
//        T = ray->origin - p0;
//
//    /* calculate U parameter and test bounds */
//        u = T * P;
//        if(u < 0.0f || u > det)
//            return 0;
//
//    /* prepare to test V parameter */
//        Q = T % E1;
//
//    /* calculate V parameter and test bounds */
//        v = ray->direction * Q;
//        if(v < 0.0f || u + v > det)
//            return 0;
//
//        det = 1./det;
//        u *= det;
//        v *= det;
//    /* calculate t, scale parameters, ray intersects triangle */
//        return E2 * Q * det;
//        #else

        float invDet = 1.0f / det;

	    /* calculate distance from vert0 to ray origin */
        T = ray.getOrigin().subtract(p0);

	    /* calculate U parameter and test bounds */
        float u = T.dot(P) * invDet;
        if (u < 0.0f || u > 1.0f) {
            return 0.f;
        }

	    /* prepare to test V parameter */
        Q = T.crossProduct(E1);

	    /* calculate V parameter and test bounds */
        float v = ray.getDirection().dot(Q) * invDet;
        if (v < 0.0f || u + v > 1.0f) {
            return 0.f;
        }

	    /* calculate t, ray intersects triangle */
        float distance = E2.dot(Q) * invDet;

//        if(ray.getTMin() > distance || ray.getTMax() < distance) {
//            return 0.f;
//        }

        return distance ;
    }

    @Override
    public Geometry transpose(Vector3 transpose) {
        return new Triangle(p0.plus(transpose), p1.plus(transpose), p2.plus(transpose));
    }

    @Override
    public Geometry rotate(ImmutableArrayMatrix3x3 rotationY) {
        return new Triangle(rotationY.mult(p0), rotationY.mult(p1), rotationY.mult(p2));
    }

    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        return n;
    }

    @Override
    public final boolean isPlane() {
        return true;
    }

    private ImmutableVector3 e2() {
        return new ImmutableVector3(p2).subtract(p0);
    }

    private ImmutableVector3 e1() {
        return new ImmutableVector3(p1).subtract(p0);
    }
}
