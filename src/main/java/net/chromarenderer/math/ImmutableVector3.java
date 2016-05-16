package net.chromarenderer.math;

import org.apache.commons.math3.util.FastMath;

/**
* @author steinerb
*/
public class ImmutableVector3 implements Vector3 {

    // using an array instead of members is up to x2 slower... investigate!
    private final float x;
    private final float y;
    private final float z;

    public ImmutableVector3(){
        this.x = 0.f;
        this.y = 0.f;
        this.z = 0.f;
    }

    public ImmutableVector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ImmutableVector3(ImmutableVector3 input) {
        this.x = input.getX();
        this.y = input.getY();
        this.z = input.getZ();
    }

    public ImmutableVector3(Vector3 input) {
        this.x = input.getX();
        this.y = input.getY();
        this.z = input.getZ();
    }


    public ImmutableVector3(double x, double y, double z) {
        this((float)x, (float)y, (float)z);
    }


    public ImmutableVector3 mult(float val){
        return new ImmutableVector3(
                x * val,
                y * val,
                z * val
        );
    }

    public float dot(Vector3 input){
        return x*input.getX() + y*input.getY() + z*input.getZ();
    }

    @Override
    public float[] internalGetValues() {
        return new float[]{x,y,z};
    }


    public ImmutableVector3 plus(Vector3 input){
        return new ImmutableVector3(
                x + input.getX(),
                y + input.getY(),
                z + input.getZ()
        );
    }

    public ImmutableVector3 plus(float x, float y, float z){
        return new ImmutableVector3(
                this.x + x,
                this.y + y,
                this.z + z
        );
    }

    public ImmutableVector3 div(float val) {
        float div = 1.0f / val;
        return mult(div);
    }

    @Override
    public ImmutableVector3 minus(Vector3 input) {
        return new ImmutableVector3(
                x - input.getX(),
                y - input.getY(),
                z - input.getZ()
        );
    }

    public ImmutableVector3 minus(float x, float y, float z) {
        return new ImmutableVector3(
                this.x - x,
                this.y - y,
                this.z - z
        );
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public ImmutableVector3 mult(Vector3 project) {
        return new ImmutableVector3(x * project.getX(), y * project.getY(), z * project.getZ());
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Vector3 input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(float x, float y, float z) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableVector3 crossProduct(Vector3 input) {
        return new ImmutableVector3(
                this.getY() * input.getZ() - this.getZ() * input.getY(),
                this.getZ() * input.getX() - this.getX() * input.getZ(),
                this.getX() * input.getY() - this.getY() * input.getX());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableVector3 vector3 = (ImmutableVector3) o;

        if (Float.compare(vector3.x, x) != 0) return false;
        if (Float.compare(vector3.y, y) != 0) return false;
        if (Float.compare(vector3.z, z) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ')';
    }

    public ImmutableVector3 normalize() {
        float recLen = 1.f / length();
        return new ImmutableVector3(getX() * recLen, getY() * recLen, getZ() * recLen);
    }

    @Override
    public ImmutableVector3 abs() {
        return new ImmutableVector3(FastMath.abs(x), FastMath.abs(y), FastMath.abs(z));
    }

}
