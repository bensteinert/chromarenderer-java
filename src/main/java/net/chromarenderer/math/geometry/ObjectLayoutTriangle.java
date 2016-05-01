package net.chromarenderer.math.geometry;

import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.shader.Material;
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
public class ObjectLayoutTriangle extends StructuredArray<ImmutableVector3> implements Triangle {

    //IDEA: Contained Vector could be merged into StructuresArray...
    private Material material;


    @SuppressWarnings("unused") // needed by ObjectLayout
    public ObjectLayoutTriangle() {
    }


    @SuppressWarnings("unused") // needed by ObjectLayout
    public ObjectLayoutTriangle(ObjectLayoutTriangle source) {
        super(source);
    }


    // newInstance wrapper for convenience (cleaner/fewer parameters for API user):
    private static ObjectLayoutTriangle newInstance(final CtorAndArgsProvider<ImmutableVector3> ctorAndArgsProvider) {
        return StructuredArray.newInstance(ObjectLayoutTriangle.class, ImmutableVector3.class, 4, ctorAndArgsProvider);
    }


    private static final ThreadLocal<ImmutableVector3CtorAndArgsProvider> threadLocalProvider = ThreadLocal.withInitial(ImmutableVector3CtorAndArgsProvider::new);


    public static ObjectLayoutTriangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2) {
        ImmutableVector3CtorAndArgsProvider provider = threadLocalProvider.get();
        provider.setP0(p0);
        provider.setP1(p1);
        provider.setP2(p2);
        provider.setN((p1).minus(p0).crossProduct((p2).minus(p0)).normalize());
        return newInstance(provider).material(Material.NULL);
    }


    public static ObjectLayoutTriangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, Material material) {
        ImmutableVector3CtorAndArgsProvider provider = threadLocalProvider.get();
        provider.setP0(p0);
        provider.setP1(p1);
        provider.setP2(p2);
        provider.setN((p1).minus(p0).crossProduct((p2).minus(p0)).normalize());
        return newInstance(provider).material(material);
    }


    private ObjectLayoutTriangle material(Material material) {
        this.material = material;
        return this;
    }


    public static ObjectLayoutTriangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n) {
        ImmutableVector3CtorAndArgsProvider provider = threadLocalProvider.get();
        provider.setP0(p0);
        provider.setP1(p1);
        provider.setP2(p2);
        provider.setN(n);
        return newInstance(provider).material(Material.NULL);
    }


    public static ObjectLayoutTriangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n, Material material) {
        ImmutableVector3CtorAndArgsProvider provider = threadLocalProvider.get();
        provider.setP0(p0);
        provider.setP1(p1);
        provider.setP2(p2);
        provider.setN(n);
        return newInstance(provider).material(material);
    }


    @Override
    public ImmutableVector3 getP0() {
        return get(0);
    }


    @Override
    public ImmutableVector3 getP1() {
        return get(1);
    }


    @Override
    public ImmutableVector3 getP2() {
        return get(2);
    }


    @Override
    public ImmutableVector3 getNormal() {
        return get(3);
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
    public ObjectLayoutTriangle transpose(Vector3 transpose) {
        return createTriangle(get(0).plus(transpose), get(1).plus(transpose), get(2).plus(transpose), material);
    }


    @Override
    public ObjectLayoutTriangle rotate(ImmutableMatrix3x3 rotationY) {
        return createTriangle(rotationY.mult(get(0)), rotationY.mult(get(1)), rotationY.mult(get(2)), material);
    }


    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        return get(3);
    }


    @Override
    public Material getMaterial() {
        return material;
    }

    public ObjectLayoutTriangle[] subdivide() {
        ObjectLayoutTriangle[] result = new ObjectLayoutTriangle[4];

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
