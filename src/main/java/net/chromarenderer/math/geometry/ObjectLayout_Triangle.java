package net.chromarenderer.math.geometry;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableArrayMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.math.shader.Material;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import org.ObjectLayout.ConstructionContext;
import org.ObjectLayout.CtorAndArgs;
import org.ObjectLayout.CtorAndArgsProvider;
import org.ObjectLayout.StructuredArray;

/**
 * Chroma uses the right-hand-coordinate system. Think about three vertices specified counterclockwise on the floor.
 * The normal will always point upwards!
 *
 * @author steinerb
 */
@SuppressWarnings("Duplicates")
public class ObjectLayout_Triangle extends StructuredArray<ImmutableVector3> implements Geometry {


    private Material material;


    public ObjectLayout_Triangle() {
    }


    public ObjectLayout_Triangle(ObjectLayout_Triangle source) {
        super(source);
    }


    // newInstance wrapper for convenience (cleaner/fewer parameters for API user):
    public static ObjectLayout_Triangle newInstance(final CtorAndArgsProvider<ImmutableVector3> ctorAndArgsProvider) {
        return StructuredArray.newInstance(ObjectLayout_Triangle.class, ImmutableVector3.class, 4, ctorAndArgsProvider);
    }


    private static ImmutableVector3CtorAndArgsProvider provider = new ImmutableVector3CtorAndArgsProvider();


    public static ObjectLayout_Triangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2) {
        provider.setP0(p0);
        provider.setP1(p1);
        provider.setP2(p2);
        provider.setN((p1).minus(p0).crossProduct((p2).minus(p0)).normalize());
        return newInstance(provider).material(Material.NULL);
    }

    public static ObjectLayout_Triangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, Material material) {
        provider.setP0(p0);
        provider.setP1(p1);
        provider.setP2(p2);
        provider.setN((p1).minus(p0).crossProduct((p2).minus(p0)).normalize());
        return newInstance(provider).material(material);
    }


    private ObjectLayout_Triangle material(Material material) {
        this.material = material;
        return this;
    }


    public static ObjectLayout_Triangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n) {
        provider.setP0(p0);
        provider.setP1(p1);
        provider.setP2(p2);
        provider.setN(n);
        return newInstance(provider).material(Material.NULL);
    }

    public static ObjectLayout_Triangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n, Material material) {
        provider.setP0(p0);
        provider.setP1(p1);
        provider.setP2(p2);
        provider.setN(n);
        return newInstance(provider).material(material);
    }


    private static class ImmutableVector3CtorAndArgsProvider implements CtorAndArgsProvider<ImmutableVector3> {

        private final ImmutableVector3[] vectors = new ImmutableVector3[4];

        void setP0(ImmutableVector3 p0) {
            this.vectors[0] = p0;
        }


        void setP1(ImmutableVector3 p1) {
            this.vectors[1] = p1;
        }


        void setP2(ImmutableVector3 p2) {
            this.vectors[2] = p2;
        }


        void setN(ImmutableVector3 n) {
            this.vectors[3] = n;
        }


        @Override
        public CtorAndArgs<ImmutableVector3> getForContext(ConstructionContext<ImmutableVector3> context) throws NoSuchMethodException {
            return new CtorAndArgs<>(ImmutableVector3.class.getConstructor(ImmutableVector3.class), vectors[(int) context.getIndex()]);
        }
    }


    @Override
    public float intersect(Ray ray) {
        return internalIntersect1(ray);
    }



    private float internalIntersect1(Ray ray) {
        ImmutableVector3 P, Q, T;
        float det;

        /* if we hit the triangle from the back, it is not a valid intersection! */
        float backFaceCulling = ray.getDirection().dot(get(3));
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
        T = ray.getOrigin().minus(get(0));

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
    public ObjectLayout_Triangle transpose(Vector3 transpose) {
        return createTriangle(get(0).plus(transpose), get(1).plus(transpose), get(2).plus(transpose), material);
    }


    @Override
    public ObjectLayout_Triangle rotate(ImmutableArrayMatrix3x3 rotationY) {
        return createTriangle(rotationY.mult(get(0)), rotationY.mult(get(1)), rotationY.mult(get(2)), material);
    }


    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        return get(3);
    }


    @Override
    public final boolean isPlane() {
        return true;
    }


    @Override
    public Material getMaterial() {
        return material;
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
        return new ImmutableVector3(get(0).mult(alpha).plus(get(1).mult(beta)).plus(get(2).mult(gamma)));
    }


    @Override
    public ImmutableVector3 getSpatialMinimum() {
        // TODO: BENCHMARK two ctor calls for new vectors ... avoid with one mutable?
        return VectorUtils.minVector(get(0), VectorUtils.minVector(get(1), get(2)));
    }


    @Override
    public ImmutableVector3 getSpatialMaximum() {
        // TODO: BENCHMARK two ctor calls for new vectors ... avoid with one mutable?
        return VectorUtils.maxVector(get(0), VectorUtils.maxVector(get(1), get(2)));
    }


    private ImmutableVector3 e3() {
        return (get(2)).minus(get(1));
    }


    private ImmutableVector3 e2() {
        return (get(2)).minus(get(0));
    }


    private ImmutableVector3 e1() {
        return (get(1)).minus(get(0));
    }


    public ObjectLayout_Triangle[] subdivide() {
        ObjectLayout_Triangle[] result = new ObjectLayout_Triangle[4];

        ImmutableVector3 p_e1 = get(0).plus(get(1)).mult(0.5f);
        ImmutableVector3 p_e2 = get(0).plus(get(2)).mult(0.5f);
        ImmutableVector3 p_e3 = get(1).plus(get(2)).mult(0.5f);

        //T1
        ImmutableVector3 p0_1 = get(0);
        ImmutableVector3 p1_1 = p_e1;
        ImmutableVector3 p2_1 = p_e2;
        //T2
        ImmutableVector3 p0_2 = p_e1;
        ImmutableVector3 p1_2 = get(1);
        ImmutableVector3 p2_2 = p_e3;
        //T3
        ImmutableVector3 p0_3 = p_e3;
        ImmutableVector3 p1_3 = get(2);
        ImmutableVector3 p2_3 = p_e2;
        //T4
        ImmutableVector3 p0_4 = p_e1;
        ImmutableVector3 p1_4 = p_e3;
        ImmutableVector3 p2_4 = p_e2;

        result[0] = createTriangle(p0_1, p1_1, p2_1, get(3), getMaterial());
        result[1] = createTriangle(p0_2, p1_2, p2_2, get(3), getMaterial());
        result[2] = createTriangle(p0_3, p1_3, p2_3, get(3), getMaterial());
        result[3] = createTriangle(p0_4, p1_4, p2_4, get(3), getMaterial());

        return result;
    }
}
