package net.chromarenderer.math.geometry;

import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.renderer.shader.Material;

/**
 * ChromaCore uses the right-hand-coordinate system. Think about three vertices specified counterclockwise on the floor.
 * The normal will always point upwards!
 *
 * @author bensteinert
 */
public class SimpleTriangle extends AbstractGeometry implements Triangle {

    private final ImmutableVector3 p0, p1, p2;
    private final ImmutableVector3 n;


    public SimpleTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n, Material material) {
        super(material);
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n = n;
    }


    public SimpleTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, ImmutableVector3 n) {
        this(p0, p1, p2, n, Material.NULL);
    }


    public SimpleTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, Material material) {
        super(material);
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n = e1().crossProduct(e2()).normalize();
    }


    public SimpleTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2) {
        this(p0, p1, p2, Material.NULL);
    }


    @Override
    public SimpleTriangle transpose(Vector3 transpose) {
        return new SimpleTriangle(p0.plus(transpose), p1.plus(transpose), p2.plus(transpose), getMaterial());
    }


    @Override
    public SimpleTriangle rotate(ImmutableMatrix3x3 rotationY) {
        return new SimpleTriangle(rotationY.mult(p0), rotationY.mult(p1), rotationY.mult(p2), getMaterial());
    }


    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        return n;
    }


    @Override
    public ImmutableVector3 getP0() {
        return p0;
    }


    @Override
    public ImmutableVector3 getP1() {
        return p1;
    }


    @Override
    public ImmutableVector3 getP2() {
        return p2;
    }


    @Override
    public ImmutableVector3 getNormal() {
        return n;
    }

    public SimpleTriangle[] subdivide() {
        SimpleTriangle[] result = new SimpleTriangle[4];

        ImmutableVector3 p_e1 = getP0().plus(getP1()).mult(0.5f);
        ImmutableVector3 p_e2 = getP0().plus(getP2()).mult(0.5f);
        ImmutableVector3 p_e3 = getP1().plus(getP2()).mult(0.5f);

        //T1
        ImmutableVector3 p0_1 = getP0();
        ImmutableVector3 p1_1 = p_e1;
        ImmutableVector3 p2_1 = p_e2;
        //T2
        ImmutableVector3 p0_2 = p_e1;
        ImmutableVector3 p1_2 = getP1();
        ImmutableVector3 p2_2 = p_e3;
        //T3
        ImmutableVector3 p0_3 = p_e3;
        ImmutableVector3 p1_3 = getP2();
        ImmutableVector3 p2_3 = p_e2;
        //T4
        ImmutableVector3 p0_4 = p_e1;
        ImmutableVector3 p1_4 = p_e3;
        ImmutableVector3 p2_4 = p_e2;

        result[0] = new SimpleTriangle(p0_1, p1_1, p2_1, getNormal(), getMaterial());
        result[1] = new SimpleTriangle(p0_2, p1_2, p2_2, getNormal(), getMaterial());
        result[2] = new SimpleTriangle(p0_3, p1_3, p2_3, getNormal(), getMaterial());
        result[3] = new SimpleTriangle(p0_4, p1_4, p2_4, getNormal(), getMaterial());

        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTriangle triangle = (SimpleTriangle) o;

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


    public static SimpleTriangle createTriangle(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2, Material material) {
        return new SimpleTriangle(p0, p1, p2, material);
    }
}
