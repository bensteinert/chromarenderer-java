package net.chromarenderer.math;

import org.apache.commons.math3.util.FastMath;

/**
 * @author steinerb
 */
public interface Vector3 {

    ImmutableVector3 ONE = new ImmutableVector3(1.0f, 1.0f, 1.0f);
    ImmutableVector3 ZERO = new ImmutableVector3(0.0f, 0.0f, 0.0f);
    ImmutableVector3 FLT_MAX = new ImmutableVector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    ImmutableVector3 MINUS_FLT_MAX = new ImmutableVector3(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
    ImmutableVector3 X_AXIS = new ImmutableVector3(1.0f, 0.0f, 0.0f);
    ImmutableVector3 Y_AXIS = new ImmutableVector3(0.0f, 1.0f, 0.0f);
    ImmutableVector3 Z_AXIS = new ImmutableVector3(0.0f, 0.0f, 1.0f);
    ImmutableVector3 ORIGIN = new ImmutableVector3(0.0f, 0.0f, 0.0f);


    Vector3 mult(float val);

    Vector3 plus(Vector3 input);

    Vector3 div(float val);

    Vector3 minus(Vector3 input);

    default float dot(Vector3 input) {
        return getX() * input.getX() + getY() * input.getY() + getZ() * input.getZ();
    }

    default float length() {
        return (float) FastMath.sqrt(getX() * getX() + getY() * getY() + getZ() * getZ());
    }

    default float squaredLength() {
        return getX() * getX() + getY() * getY() + getZ() * getZ();
    }

    float[] internalGetValues();

    float getX();

    float getY();

    float getZ();

    Vector3 mult(Vector3 project);

    void reset();

    void set(Vector3 input);

    void set(float x, float y, float z);

    Vector3 crossProduct(Vector3 input);

    Vector3 normalize();

    Vector3 abs();

    default float getMaxValue() {
        return FastMath.max(getZ(), FastMath.max(getX(), getY()));
    }

    default boolean nonZero() {
        return getX() != 0.0f || getY() != 0.0f || getZ() != 0.0f;
    }

    default int getMinIndexAbs() {
        float absX = FastMath.abs(getX());
        float absY = FastMath.abs(getY());
        float absZ = FastMath.abs(getZ());
        if(absX < absY) {
            return (absZ < absX) ? 2 : 0;
        }
        else {
            return (absZ < absY) ? 2 : 1;
        }
    }

    default boolean isNaN(){
        return Float.isNaN(getX()) || Float.isNaN(getY()) ||Float.isNaN(getZ());
    }

    default int getMaxValueIndex(){
        if (getX() > getY()) {
            return (getZ() > getX()) ? 2 : 0;
        }
        else {
            return (getZ() > getY()) ? 2 : 1;
        }
    }

    default int getMinValueIndex(){
        if (getX() < getY()) {
            return (getX() < getZ()) ? 0 : 2;
        }
        else {
            return (getZ() < getY()) ? 2 : 1;
        }
    }

    default float getScalar(int splitDimIndex){
        return internalGetValues()[splitDimIndex];
    }

    default boolean isCloserToOriginThan(Vector3 otherVector){
        return this.getX() < otherVector.getX()
                && this.getY() < otherVector.getY()
                && this.getZ() < otherVector.getZ();
    }
}
