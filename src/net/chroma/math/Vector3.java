package net.chroma.math;

/**
 * @author steinerb
 */
public interface Vector3 {

    Vector3 mult(float val);

    Vector3 plus(Vector3 input);

    Vector3 div(float val);

    float getX();

    float getY();

    float getZ();

    Vector3 mult(Vector3 project);

    void reset();

    void set(Vector3 input);

    void set(float x, float y, float z);
}
