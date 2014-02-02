package net.chroma.math;

/**
 * @author steinerb
 */
public interface Vector3 {

    Vector3 mult(float val);

    Vector3 plus(Vector3 input);

    Vector3 div(float val);

    Vector3 subtract(Vector3 input);

    default float dot(Vector3 input) {
        return getX() * input.getX() + getY() * input.getY() + getZ() * input.getZ();
    }

    float getX();

    float getY();

    float getZ();

    Vector3 mult(Vector3 project);

    void reset();

    void set(Vector3 input);

    void set(float x, float y, float z);

    Vector3 crossProduct(Vector3 input);
}
