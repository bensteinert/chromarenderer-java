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
}
