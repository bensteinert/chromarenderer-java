package net.chromarenderer.math.raytracing;

import net.chromarenderer.math.ImmutableVector3;

/**
 * @author steinerb
 */
public class CoordinateSystem {

    private final ImmutableVector3 t1;
    private final ImmutableVector3 t2;
    private final ImmutableVector3 n;


    public CoordinateSystem(ImmutableVector3 t1, ImmutableVector3 t2, ImmutableVector3 n) {
        this.t1 = t1;
        this.t2 = t2;
        this.n = n;
    }


    public ImmutableVector3 getT1() {
        return t1;
    }


    public ImmutableVector3 getT2() {
        return t2;
    }


    public ImmutableVector3 getN() {
        return n;
    }
}
