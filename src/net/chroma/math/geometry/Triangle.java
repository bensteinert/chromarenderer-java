package net.chroma.math.geometry;

import net.chroma.math.ImmutableVector3;
import net.chroma.math.raytracing.Ray;

/**
 * @author steinerb
 */
public class Triangle implements Geometry {

    private static final double EPSILON = 0.000001;

    private final ImmutableVector3 p0, p1, p2;
    private final ImmutableVector3 E1, E2;
    private final ImmutableVector3 n;

    public Triangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n = n;
        E1 = new ImmutableVector3(p1).subtract(p0);
        E2 = new ImmutableVector3(p2).subtract(p0);
    }

    public float intersect(Ray ray){

        ImmutableVector3 P, Q, T;
        float det;

        /* begin calculating determinant - also used to calculate U parameter */
        P = ray.getDirection().crossProduct(E2);


        det = E1.dot(P);

        /* if determinant is near zero, ray lies in plane of triangle */
        if(det > -EPSILON && det < EPSILON){
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
        return E2.dot(Q) * invDet;
    }
}
