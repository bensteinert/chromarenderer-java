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

    @Override
    public Vector3 mult(float val){
        values[0] *= val;
        values[1] *= val;
        values[2] *= val;
        return this;
    }

    @Override
    public Vector3 plus(Vector3 input){
        values[0] += input.getX();
        values[1] += input.getY();
        values[2] += input.getZ();
        return this;
    }

    @Override
    public Vector3 div(float val) {
        float div = 1.0f / val;
        return mult(div);
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
    public Vector3 mult(Vector3 project) {
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
}
