package net.chromarenderer.math;

import org.apache.commons.math3.util.FastMath;

import java.util.Arrays;

/**
 * @author steinerb
 */
public class MutableVector3 implements Vector3 {

    private final float[] values;

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
    public MutableVector3 minus(Vector3 input) {
        values[0] -= input.getX();
        values[1] -= input.getY();
        values[2] -= input.getZ();
        return this;
    }


    @Override
    public float[] internalGetValues() {
        return values;
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

    public void setValue(int index, float value){
        values[index] = value;
    }


    @Override
    public MutableVector3 crossProduct(Vector3 input) {
        float x = values[0];
        float y = values[1];
        float z = values[2];
        values[0] = y * input.getZ() - z * input.getY();
        values[1] = z * input.getX() - x * input.getZ();
        values[2] = x * input.getY() - y * input.getX();
        return this;
    }

    public MutableVector3 normalize() {
        float recLen = 1.f / length();
        return this.mult(recLen);
    }

    @Override
    public MutableVector3 abs() {
        values[0] = FastMath.abs(values[0]);
        values[1] = FastMath.abs(values[1]);
        values[2] = FastMath.abs(values[2]);

        return this;
    }
}
