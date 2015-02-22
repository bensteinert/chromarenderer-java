package net.chromarenderer.math.geometry;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableArrayMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.math.shader.Material;
import net.chromarenderer.renderer.core.ChromaThreadContext;

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
    public Geometry transpose(Vector3 transpose) {
        return new Triangle(p0.plus(transpose), p1.plus(transpose), p2.plus(transpose), getMaterial());
    }

    @Override
    public Geometry rotate(ImmutableArrayMatrix3x3 rotationY) {
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

    private ImmutableVector3 e2() {
        return (p2).minus(p0);
    }

    private ImmutableVector3 e1() {
        return (p1).minus(p0);
    }
}
