package net.chroma.math;

/**
 * @author steinerb
 */
public class Vector3 {

    private float x;
    private float y;
    private float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 mult(float val){
        return new Vector3(
                x * val,
                y * val,
                z * val
        );
    }

    public Vector3 plus(Vector3 input){
        return new Vector3(
                x + input.x,
                y + input.y,
                z + input.z
        );
    }

    public Vector3 div(float val) {
        return new Vector3(
                x / val,
                y / val,
                z / val
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

    public Vector3 mult(Vector3 project) {
        return new Vector3(x * project.x, y * project.y, z * project.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector3 vector3 = (Vector3) o;

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
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
