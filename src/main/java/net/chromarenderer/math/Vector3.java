package net.chromarenderer.math;

/**
 * @author steinerb
 */
public interface Vector3 {

    public static final ImmutableVector3 ONE = new ImmutableVector3(1.0f, 1.0f, 1.0f);
    public static final ImmutableVector3 X_AXIS = new ImmutableVector3(1.0f, 0.0f, 0.0f);
    public static final ImmutableVector3 Y_AXIS = new ImmutableVector3(0.0f, 1.0f, 0.0f);
    public static final ImmutableVector3 Z_AXIS = new ImmutableVector3(0.0f, 0.0f, 1.0f);
    public static final ImmutableVector3 ORIGIN = new ImmutableVector3(0.0f, 0.0f, 0.0f);


    Vector3 mult(float val);

    Vector3 plus(Vector3 input);

    Vector3 div(float val);

    Vector3 minus(Vector3 input);

    default float dot(Vector3 input) {
        return getX() * input.getX() + getY() * input.getY() + getZ() * input.getZ();
    }

    default float length() {
        return (float) Math.sqrt(getX() * getX() + getY() * getY() + getZ() * getZ());
    }

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

    default public float getMaxValue() {
        return Math.max(getZ(), Math.max(getX(), getY()));
    }

    default public boolean nonZero() {
        return getX() != 0.0f || getY() != 0.0f || getZ() != 0.0f;
    }

    default public int getMinIndexAbs() {
        float absX = Math.abs(getX());
        float absY = Math.abs(getY());
        float absZ = Math.abs(getZ());
        if(absX < absY) {
            return (absZ < absX) ? 2 : 0;
        }
        else {
            return (absZ < absY) ? 2 : 1;
        }
    }

    default public boolean isNaN(){
        return Float.isNaN(getX()) || Float.isNaN(getY()) ||Float.isNaN(getZ());
    }
}
