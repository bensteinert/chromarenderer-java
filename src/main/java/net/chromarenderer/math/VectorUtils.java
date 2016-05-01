package net.chromarenderer.math;

import org.apache.commons.math3.util.FastMath;

/**
 * @author steinerb
 */
public class VectorUtils {

    public static ImmutableVector3 mirror(ImmutableVector3 direction, ImmutableVector3 normal) {
        //R = 2 * N * dot(N, OV) - OV;
        float projectedLength = normal.dot(direction);
        return normal.mult(2.0f * projectedLength).minus(direction);
    }

    public static ImmutableVector3 maxVector(Vector3 p0, Vector3 p1) {
        return new ImmutableVector3(
                FastMath.max(p0.getX(), p1.getX()),
                FastMath.max(p0.getY(), p1.getY()),
                FastMath.max(p0.getZ(), p1.getZ())
        );
    }

    public static ImmutableVector3 minVector(Vector3 p0, Vector3 p1) {
        return new ImmutableVector3(
                FastMath.min(p0.getX(), p1.getX()),
                FastMath.min(p0.getY(), p1.getY()),
                FastMath.min(p0.getZ(), p1.getZ())
        );
    }
}
