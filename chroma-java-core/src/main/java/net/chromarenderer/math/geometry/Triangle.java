package net.chromarenderer.math.geometry;

import net.chromarenderer.main.ChromaStatistics;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import org.apache.commons.math3.util.FastMath;

/**
 * @author bensteinert
 */
public interface Triangle extends Geometry {

    ImmutableVector3 getP0();

    ImmutableVector3 getP1();

    ImmutableVector3 getP2();

    ImmutableVector3 getNormal();

    Triangle transpose(Vector3 transpose);

    Triangle rotate(ImmutableMatrix3x3 rotationY);

    Triangle[] subdivide();

    default ImmutableVector3 e3() {
        return (getP2()).minus(getP1());
    }

    default ImmutableVector3 e2() {
        return (getP2()).minus(getP0());
    }

    default ImmutableVector3 e1() {
        return (getP1()).minus(getP0());
    }


    default float intersect(Ray ray) {
        ChromaStatistics.intersectOp();
        ImmutableVector3 P, Q, T;
        float det;

        /*
        * If we hit the triangle from the back, usually it is not a valid intersection!
        * Except if the ray passed the geometry due to shading.
        **/
        float backFaceCulling = ray.getDirection().dot(getNormal());
        if (backFaceCulling > 0.0f && !ray.isTransparent()) {
            return 0.f;
        }

        ImmutableVector3 E1 = e1();
        ImmutableVector3 E2 = e2();

        /* begin calculating determinant - also used to calculate U parameter */
        P = ray.getDirection().crossProduct(E2);

        det = E1.dot(P);

        /* if determinant is near zero, ray lies in plane of triangle */
        if (det > -Constants.FLT_EPSILON && det < Constants.FLT_EPSILON) {
            return 0.f;
        }

        float invDet = 1.0f / det;

	    /* calculate distance from vert0 to ray origin */
        T = ray.getOrigin().minus(getP0());

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

	    /* calculate distance, ray intersects triangle */
        return E2.dot(Q) * invDet;
    }

//    private float internalIntersectPlainFloat(Ray ray) {
//        ImmutableVector3 rayDir = ray.getDirection();
//
//        /* if we hit the triangle from the back, it is not a valid intersection! */
//        float backFaceCulling = rayDir.dot(n); // no ctor involved
//        if (backFaceCulling > 0.0f) {
//            return 0.f;
//        }
//
//        float e1_x = p1.getX() - p0.getX();
//        float e1_y = p1.getY() - p0.getY();
//        float e1_z = p1.getZ() - p0.getZ();
//        float e2_x = p2.getX() - p0.getX();
//        float e2_y = p2.getY() - p0.getY();
//        float e2_z = p2.getZ() - p0.getZ();
//
//        /* begin calculating determinant - also used to calculate U parameter */
//        float P_x = rayDir.getY() * e2_z - rayDir.getZ() * e2_y;
//        float P_y = rayDir.getZ() * e2_x - rayDir.getX() * e2_z;
//        float P_z = rayDir.getX() * e2_y - rayDir.getY() * e2_x;
//
//        float det = e1_x * P_x + e1_y * P_y + e1_z * P_z;
//
//        /* if determinant is near zero, ray lies in plane of triangle */
//        if (det > -Constants.FLT_EPSILON && det < Constants.FLT_EPSILON) {
//            return 0.f;
//        }
//
//        float invDet = 1.0f / det;
//
//	    /* calculate distance from vert0 to ray origin */
//        ImmutableVector3 rayOrigin = ray.getOrigin();
//
//        float T_x = rayOrigin.getX() - p0.getX();
//        float T_y = rayOrigin.getY() - p0.getY();
//        float T_z = rayOrigin.getZ() - p0.getZ();
//
//
//	    /* calculate U parameter and test bounds */
//        float u = (T_x * P_x + T_y * P_y + T_z * P_z) * invDet;
//        if (u < 0.0f || u > 1.0f) {
//            return 0.f;
//        }
//
//	    /* prepare to test V parameter */
//        float Q_x = T_y * e1_z - T_z * e1_y;
//        float Q_y = T_z * e1_x - T_x * e1_z;
//        float Q_z = T_x * e1_y - T_y * e1_x;
//
//	    /* calculate V parameter and test bounds */
//        float v = (rayDir.getX() * Q_x + rayDir.getY() * Q_y + rayDir.getZ() * Q_z) * invDet;
//
//        if (v < 0.0f || u + v > 1.0f) {
//            return 0.f;
//        }
//
//	    /* calculate distance, ray intersects triangle */
//        float distance = (e2_x * Q_x + e2_y * Q_y + e2_z * Q_z) * invDet;
//
//        if (ray.getTMin() > distance || ray.getTMax() < distance) {
//            return 0.f;
//        }
//
//        return distance;
//    }

    @Override
    default ImmutableVector3 getUnifDistrSample() {
        float u = ChromaThreadContext.randomFloatClosedOpen();
        float v = ChromaThreadContext.randomFloatClosedOpen();
        float sqrtU = (float) FastMath.sqrt(u);
        float alpha = 1.0f - sqrtU;
        float beta = (1.0f - v) * sqrtU;
        float gamma = v * sqrtU;
        return new ImmutableVector3(getP0().mult(alpha).plus(getP1().mult(beta)).plus(getP2().mult(gamma)));
    }

    @Override
    default ImmutableVector3 getSpatialMinimum() {
        // TODO: BENCHMARK two ctor calls for new vectors ... avoid with one mutable?
        return VectorUtils.minVector(getP0(), VectorUtils.minVector(getP1(), getP2()));
    }

    @Override
    default ImmutableVector3 getSpatialMaximum() {
        // TODO: BENCHMARK two ctor calls for new vectors ... avoid with one mutable?
        return VectorUtils.maxVector(getP0(), VectorUtils.maxVector(getP1(), getP2()));
    }


    @Override
    default boolean isPlane() {
        return true;
    }

    @Override
    default float getArea() {
        ImmutableVector3 edgeCrossProduct = e1().crossProduct(e2());
        return edgeCrossProduct.length() * 0.5f;
    }
}
