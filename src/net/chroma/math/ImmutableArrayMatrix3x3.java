package net.chroma.math;

/**
 * @author steinerb
 */
public class ImmutableArrayMatrix3x3 {

    private final float m11, m12, m13;
    private final float m21, m22, m23;
    private final float m31, m32, m33;


    public ImmutableArrayMatrix3x3(float m11, float m12, float m13, float m21, float m22, float m23, float m31, float m32, float m33) {
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }


    public ImmutableArrayMatrix3x3 invert() {
        float invDet = 1.f/(m11*m22*m33 + m12*m23*m31 + m13*m21*m32 - m12*m21*m33 - m13*m22*m31 - m11*m23*m32);

        float newm11 = (m22*m33 - m23*m32) * invDet;
        float newm12 = (m13*m32 - m12*m33) * invDet;
        float newm13 = (m12*m23 - m13*m22) * invDet;
        float newm21 = (m23*m31 - m33*m21) * invDet;
        float newm22 = (m11*m33 - m13*m31) * invDet;
        float newm23 = (m13*m21 - m11*m23) * invDet;
        float newm31 = (m21*m32 - m31*m22) * invDet;
        float newm32 = (m12*m31 - m11*m32) * invDet;
        float newm33 = (m11*m22 - m12*m21) * invDet;

        return new ImmutableArrayMatrix3x3(newm11, newm12, newm13, newm21, newm22, newm23, newm31, newm32, newm33);
    }

    public ImmutableArrayMatrix3x3 orthogonalize() {
        ImmutableVector3 tempA2, tempA3;
        ImmutableVector3 newCol1 = col1().normalize();
        tempA2 = col2().subtract(newCol1.mult(newCol1.dot(col2())));
        ImmutableVector3 newCol2 = tempA2.normalize();
        ImmutableVector3 projNewCol1 = newCol1.mult(newCol1.dot(col3()));
        ImmutableVector3 projNewCol2 = newCol2.mult(newCol2.dot(col3()));
        tempA3 = col3().subtract(projNewCol1).subtract(projNewCol2);
        ImmutableVector3 newCol3 = tempA3.normalize();      
        return new ImmutableArrayMatrix3x3(newCol1.getX(), newCol2.getX(), newCol3.getX(),
                                           newCol1.getY(), newCol2.getY(), newCol3.getY(),
                                           newCol1.getZ(), newCol2.getZ(), newCol3.getZ());
    }

    public ImmutableArrayMatrix3x3 transpose() {
        return new ImmutableArrayMatrix3x3(m11, m21, m31,
                                           m12, m22, m32,
                                           m13, m23, m33);
    }

    public ImmutableArrayMatrix3x3 mult(ImmutableArrayMatrix3x3 input) {

        return new ImmutableArrayMatrix3x3((m11 * input.m11) + (m21 * input.m21) + (m13 * input.m31), (m11 * input.m12) + (m21 * input.m22) + (m13 * input.m32), (m11 * input.m13) + (m21 * input.m23) + (m13 * input.m33),
                                           (m21 * input.m11) + (m22 * input.m21) + (m23 * input.m31), (m21 * input.m12) + (m22 * input.m22) + (m23 * input.m32), (m21 * input.m13) + (m22 * input.m23) + (m23 * input.m33),
                                           (m31 * input.m11) + (m32 * input.m21) + (m33 * input.m31), (m31 * input.m12) + (m32 * input.m22) + (m33 * input.m32), (m31 * input.m13) + (m32 * input.m23) + (m33 * input.m33));
    }


    public ImmutableVector3 mult(ImmutableVector3 input) {
        return new ImmutableVector3( row1().dot(input), row2().dot(input), row3().dot(input) );
    }

    private ImmutableVector3 row1() {
        return new ImmutableVector3(m11, m12, m13);
    }

    private ImmutableVector3 row2() {
        return new ImmutableVector3(m21, m22, m23);
    }

    private ImmutableVector3 row3() {
        return new ImmutableVector3(m31, m32, m33);
    }

    private ImmutableVector3 col1() {
        return new ImmutableVector3(m11, m21, m31);
    }

    private ImmutableVector3 col2() {
        return new ImmutableVector3(m12, m22, m32);
    }

    private ImmutableVector3 col3() {
        return new ImmutableVector3(m13, m23, m33);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableArrayMatrix3x3 that = (ImmutableArrayMatrix3x3) o;

        if (Float.compare(that.m11, m11) != 0) return false;
        if (Float.compare(that.m12, m12) != 0) return false;
        if (Float.compare(that.m13, m13) != 0) return false;
        if (Float.compare(that.m21, m21) != 0) return false;
        if (Float.compare(that.m22, m22) != 0) return false;
        if (Float.compare(that.m23, m23) != 0) return false;
        if (Float.compare(that.m31, m31) != 0) return false;
        if (Float.compare(that.m32, m32) != 0) return false;
        if (Float.compare(that.m33, m33) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (m11 != +0.0f ? Float.floatToIntBits(m11) : 0);
        result = 31 * result + (m12 != +0.0f ? Float.floatToIntBits(m12) : 0);
        result = 31 * result + (m13 != +0.0f ? Float.floatToIntBits(m13) : 0);
        result = 31 * result + (m21 != +0.0f ? Float.floatToIntBits(m21) : 0);
        result = 31 * result + (m22 != +0.0f ? Float.floatToIntBits(m22) : 0);
        result = 31 * result + (m23 != +0.0f ? Float.floatToIntBits(m23) : 0);
        result = 31 * result + (m31 != +0.0f ? Float.floatToIntBits(m31) : 0);
        result = 31 * result + (m32 != +0.0f ? Float.floatToIntBits(m32) : 0);
        result = 31 * result + (m33 != +0.0f ? Float.floatToIntBits(m33) : 0);
        return result;
    }

    @Override
    public String toString() {
        return  "m11=" + m11 + " | m12=" + m12 + " | m13=" + m13 + "\n" +
                "m21=" + m21 + " | m22=" + m22 + " | m23=" + m23 + "\n" +
                "m31=" + m31 + " | m32=" + m32 + " | m33=" + m33 + "\n";
    }
}
