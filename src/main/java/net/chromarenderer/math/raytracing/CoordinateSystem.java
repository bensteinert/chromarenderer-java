package net.chromarenderer.math.raytracing;

import net.chromarenderer.math.Vector3;

/**
 * @author steinerb
 */
public class CoordinateSystem {

    private final Vector3 t1;
    private final Vector3 t2;
    private final Vector3 n;


    public CoordinateSystem(Vector3 t1, Vector3 t2, Vector3 n) {
        this.t1 = t1;
        this.t2 = t2;
        this.n = n;
    }


    public Vector3 getT1() {
        return t1;
    }


    public Vector3 getT2() {
        return t2;
    }


    public Vector3 getN() {
        return n;
    }
}
