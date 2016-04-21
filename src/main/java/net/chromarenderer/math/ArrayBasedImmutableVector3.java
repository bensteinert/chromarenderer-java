package net.chromarenderer.math;

import java.util.Arrays;

/**
* @author steinerb
*/
public class ArrayBasedImmutableVector3 implements Vector3 {

    private final float[] values;

    public ArrayBasedImmutableVector3() {
        values = new float[]{0.f,0.f,0.f};
    }

    public ArrayBasedImmutableVector3(float x, float y, float z) {
        values = new float[]{x,y,z};
    }

    public ArrayBasedImmutableVector3(Vector3 input) {
        values = new float[]{input.getX(), input.getY(),input.getZ()};
    }

    public ArrayBasedImmutableVector3 mult(float val){
        return new ArrayBasedImmutableVector3(
                values[0] * val,
                values[1] * val,
                values[2] * val
        );
    }

    public float dot(Vector3 input){
        return values[0]*input.getX() + values[1]*input.getY() + values[2]*input.getZ();
    }


    @Override
    public float[] internalGetValues() {
        return Arrays.copyOf(values, 3);
    }


    public ArrayBasedImmutableVector3 plus(Vector3 input){
        return new ArrayBasedImmutableVector3(
                values[0] + input.getX(),
                values[1] + input.getY(),
                values[2] + input.getZ()
        );
    }

    public ArrayBasedImmutableVector3 plus(float x, float y, float z){
        return new ArrayBasedImmutableVector3(
                this.values[0] + x,
                this.values[1] + y,
                this.values[2] + z
        );
    }

    public ArrayBasedImmutableVector3 div(float val) {
        float div = 1.0f / val;
        return mult(div);
    }

    @Override
    public ArrayBasedImmutableVector3 minus(Vector3 input) {
        return new ArrayBasedImmutableVector3(
                values[0] - input.getX(),
                values[1] - input.getY(),
                values[2] - input.getZ()
        );
    }

    public ArrayBasedImmutableVector3 minus(float x, float y, float z) {
        return new ArrayBasedImmutableVector3(
                this.values[0] - x,
                this.values[1] - y,
                this.values[2] - z
        );
    }

    public float getX() {
        return values[0];
    }

    public float getY() {
        return values[1];
    }

    public float getZ() {
        return values[2];
    }

    public Vector3 mult(Vector3 project) {
        return new ArrayBasedImmutableVector3(values[0] * project.getX(), values[1] * project.getY(), values[2] * project.getZ());
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
    public ArrayBasedImmutableVector3 crossProduct(Vector3 input) {
        return new ArrayBasedImmutableVector3(
                this.getY() * input.getZ() - this.getZ() * input.getY(),
                this.getZ() * input.getX() - this.getX() * input.getZ(),
                this.getX() * input.getY() - this.getY() * input.getX());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayBasedImmutableVector3 vector3 = (ArrayBasedImmutableVector3) o;

        if (Float.compare(vector3.values[0], values[0]) != 0) return false;
        if (Float.compare(vector3.values[1], values[1]) != 0) return false;
        if (Float.compare(vector3.values[2], values[2]) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (values[0] != +0.0f ? Float.floatToIntBits(values[0]) : 0);
        result = 31 * result + (values[1] != +0.0f ? Float.floatToIntBits(values[1]) : 0);
        result = 31 * result + (values[2] != +0.0f ? Float.floatToIntBits(values[2]) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + values[0] +
                ", y=" + values[1] +
                ", z=" + values[2] +
                '}';
    }

    public ArrayBasedImmutableVector3 normalize() {
        float recLen = 1.f / length();
        return new ArrayBasedImmutableVector3(getX() * recLen, getY() * recLen, getZ() * recLen);
    }

    @Override
    public ArrayBasedImmutableVector3 abs() {
        return new ArrayBasedImmutableVector3(Math.abs(values[0]), Math.abs(values[1]), Math.abs(values[2]));
    }


    @Override
    public float getScalar(int splitDimIndex) {
        return values[splitDimIndex];
    }


    public ArrayBasedImmutableVector3 inverse() {
        return new ArrayBasedImmutableVector3(1.f / this.getX(), 1.f/this.getY(), 1.f/this.getZ()).normalize();
    }
}
