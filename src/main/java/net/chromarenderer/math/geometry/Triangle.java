package net.chromarenderer.math.geometry;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.math.shader.Material;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.main.ChromaStatistics;

/**
 * Chroma uses the right-hand-coordinate system. Think about three vertices specified counterclockwise on the floor.
 * The normal will always point upwards!
 *
 * @author steinerb
 */
public class Triangle extends AbstractGeometry {

    private final ImmutableVector3 p0, p1, p2;
    private final ImmutableVector3 n;

    public Triangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n, Material material) {
        super(material);
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n = n;
    }

    public Triangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n) {
        this(p0, p1, p2, n, Material.NULL);
    }

    public Triangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, Material material) {
        super(material);
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n = e1().crossProduct(e2()).normalize();
    }

    public Triangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2) {
        this(p0, p1, p2, Material.NULL);
    }

    @Override
    public float intersect(Ray ray) {
        ChromaStatistics.intersectOp();
        return internalIntersectPlainFloat(ray);
    }


    private float internalIntersectPlainFloat(Ray ray) {
        ImmutableVector3 rayDir = ray.getDirection();

        /* if we hit the triangle from the back, it is not a valid intersection! */
        float backFaceCulling = rayDir.dot(n); // no ctor involved
        if (backFaceCulling > 0.0f) {
            return 0.f;
        }

        float e1_x = p1.getX() - p0.getX();
        float e1_y = p1.getY() - p0.getY();
        float e1_z = p1.getZ() - p0.getZ();
        float e2_x = p2.getX() - p0.getX();
        float e2_y = p2.getY() - p0.getY();
        float e2_z = p2.getZ() - p0.getZ();

        /* begin calculating determinant - also used to calculate U parameter */
        float P_x = rayDir.getY() * e2_z - rayDir.getZ() * e2_y;
        float P_y = rayDir.getZ() * e2_x - rayDir.getX() * e2_z;
        float P_z = rayDir.getX() * e2_y - rayDir.getY() * e2_x;

        float det = e1_x*P_x + e1_y*P_y + e1_z*P_z;

        /* if determinant is near zero, ray lies in plane of triangle */
        if (det > -Constants.FLT_EPSILON && det < Constants.FLT_EPSILON) {
            return 0.f;
        }

        float invDet = 1.0f / det;

	    /* calculate distance from vert0 to ray origin */
        ImmutableVector3 rayOrigin = ray.getOrigin();

        float T_x = rayOrigin.getX() - p0.getX();
        float T_y = rayOrigin.getY() - p0.getY();
        float T_z = rayOrigin.getZ() - p0.getZ();


	    /* calculate U parameter and test bounds */
        float u =  (T_x*P_x + T_y*P_y + T_z*P_z) * invDet;
        if (u < 0.0f || u > 1.0f) {
            return 0.f;
        }

	    /* prepare to test V parameter */
        float Q_x = T_y * e1_z - T_z * e1_y;
        float Q_y = T_z * e1_x - T_x * e1_z;
        float Q_z = T_x * e1_y - T_y * e1_x;

	    /* calculate V parameter and test bounds */
        float v = (rayDir.getX()*Q_x + rayDir.getY()*Q_y + rayDir.getZ()*Q_z) * invDet;

        if (v < 0.0f || u + v > 1.0f) {
            return 0.f;
        }

	    /* calculate distance, ray intersects triangle */
        float distance = (e2_x*Q_x + e2_y*Q_y + e2_z*Q_z) * invDet;

        if (ray.getTMin() > distance || ray.getTMax() < distance) {
            return 0.f;
        }

        return distance;
    }


    private float internalIntersect(Ray ray) {
        ImmutableVector3 P, Q, T;
        float det;

        /* if we hit the triangle from the back, it is not a valid intersection! */
        float backFaceCulling = ray.getDirection().dot(n);
        if (backFaceCulling > 0.0f) {
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
        T = ray.getOrigin().minus(p0);

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
        float distance = E2.dot(Q) * invDet;

        if (ray.getTMin() > distance || ray.getTMax() < distance) {
            return 0.f;
        }

        return distance;
    }


    @Override
    public Triangle transpose(Vector3 transpose) {
        return new Triangle(p0.plus(transpose), p1.plus(transpose), p2.plus(transpose), getMaterial());
    }

    @Override
    public Triangle rotate(ImmutableMatrix3x3 rotationY) {
        return new Triangle(rotationY.mult(p0), rotationY.mult(p1), rotationY.mult(p2), getMaterial());
    }

    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        return n;
    }

    @Override
    public final boolean isPlane() {
        return true;
    }

    @Override
    public float getArea() {
        ImmutableVector3 edgeCrossProduct = e1().crossProduct(e2());
        return edgeCrossProduct.length() * 0.5f;
    }

    @Override
    public ImmutableVector3 getUnifDistrSample() {
        float u = ChromaThreadContext.randomFloatClosedOpen();
        float v = ChromaThreadContext.randomFloatClosedOpen();
        float sqrtU = (float) Math.sqrt(u);
        float alpha = 1.0f - sqrtU;
        float beta = (1.0f - v) * sqrtU;
        float gamma = v * sqrtU;
        return new ImmutableVector3(p0.mult(alpha).plus(p1.mult(beta)).plus(p2.mult(gamma)));
    }

    @Override
    public ImmutableVector3 getSpatialMinimum() {
        // TODO: BENCHMARK two ctor calls for new vectors ... avoid with one mutable?
        return VectorUtils.minVector(p0, VectorUtils.minVector(p1, p2));
    }

    @Override
    public ImmutableVector3 getSpatialMaximum() {
        // TODO: BENCHMARK two ctor calls for new vectors ... avoid with one mutable?
        return VectorUtils.maxVector(p0, VectorUtils.maxVector(p1, p2));
    }


    private ImmutableVector3 e3() {
        return (p2).minus(p1);
    }

    private ImmutableVector3 e2() {
        return (p2).minus(p0);
    }

    private ImmutableVector3 e1() {
        return (p1).minus(p0);
    }



    public Triangle[] subdivide() {
        Triangle[] result = new Triangle[4];

        ImmutableVector3 p_e1 = p0.plus(p1).mult(0.5f);
        ImmutableVector3 p_e2 = p0.plus(p2).mult(0.5f);
        ImmutableVector3 p_e3 = p1.plus(p2).mult(0.5f);

        //T1
        ImmutableVector3 p0_1 = p0;
        ImmutableVector3 p1_1 = p_e1;
        ImmutableVector3 p2_1 = p_e2;
        //T2
        ImmutableVector3 p0_2 = p_e1;
        ImmutableVector3 p1_2 = p1;
        ImmutableVector3 p2_2 = p_e3;
        //T3
        ImmutableVector3 p0_3 = p_e3;
        ImmutableVector3 p1_3 = p2;
        ImmutableVector3 p2_3 = p_e2;
        //T4
        ImmutableVector3 p0_4 = p_e1;
        ImmutableVector3 p1_4 = p_e3;
        ImmutableVector3 p2_4 = p_e2;

        result[0] = new Triangle(p0_1, p1_1, p2_1, n, getMaterial());
        result[1] = new Triangle(p0_2, p1_2, p2_2, n, getMaterial());
        result[2] = new Triangle(p0_3, p1_3, p2_3, n, getMaterial());
        result[3] = new Triangle(p0_4, p1_4, p2_4, n, getMaterial());

        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triangle triangle = (Triangle) o;

        if (!p0.equals(triangle.p0)) return false;
        if (!p1.equals(triangle.p1)) return false;
        return p2.equals(triangle.p2);

    }


    @Override
    public int hashCode() {
        int result = p0.hashCode();
        result = 31 * result + p1.hashCode();
        result = 31 * result + p2.hashCode();
        return result;
    }
}
