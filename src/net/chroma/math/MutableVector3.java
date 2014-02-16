package net.chroma.math;

import java.util.Arrays;

/**
 * @author steinerb
 */
public class MutableVector3 implements Vector3 {

    private float[] values;

    public MutableVector3() {
        values = new float[3];
        reset();
    }

    public MutableVector3(float x, float y, float z) {
        values = new float[3];
        values[0] = x;
        values[1] = y;
        values[2] = z;
    }

    public MutableVector3(Vector3 input) {
        values = new float[3];
        values[0] = input.getX();
        values[1] = input.getY();
        values[2] = input.getZ();
    }

    @Override
    public MutableVector3 mult(float val){
        values[0] *= val;
        values[1] *= val;
        values[2] *= val;
        return this;
    }

    @Override
    public MutableVector3 plus(Vector3 input){
        values[0] += input.getX();
        values[1] += input.getY();
        values[2] += input.getZ();
        return this;
    }

    @Override
    public MutableVector3 div(float val) {
        float div = 1.0f / val;
        return mult(div);
    }

    @Override
    public MutableVector3 subtract(Vector3 input) {
        values[0] -= input.getX();
        values[1] -= input.getY();
        values[2] -= input.getZ();
        return this;
    }

    @Override
    public float getX() {
        return values[0];
    }

    @Override
    public float getY() {
        return values[1];
    }

    @Override
    public float getZ() {
        return values[2];
    }

    @Override
    public MutableVector3 mult(Vector3 project) {
        values[0] *= project.getX();
        values[1] *= project.getY();
        values[2] *= project.getZ();
        return this;
    }

    @Override
    public void reset() {
        Arrays.fill(values, 0.0f);
    }

    @Override
    public void set(Vector3 input) {
        values[0] = input.getX();
        values[1] = input.getY();
        values[2] = input.getZ();
    }

    @Override
    public void set(float x, float y, float z) {
        values[0] = x;
        values[1] = y;
        values[2] = z;
    }

    @Override
    public MutableVector3 crossProduct(Vector3 input) {
        values[0] = this.getY() * input.getZ() - this.getZ() * input.getY();
        values[1] = this.getZ() * input.getX() - this.getX() * input.getZ();
        values[2] = this.getX() * input.getY() - this.getY() * input.getX();
        return this;
    }

    public MutableVector3 normalize() {
        float recLen = 1.f / length();
        return this.mult(recLen);
    }

    @Override
    public MutableVector3 abs() {
        values[0] = Math.abs(values[0]);
        values[1] = Math.abs(values[1]);
        values[2] = Math.abs(values[2]);

        return this;
    }
}
