package net.chromarenderer.math;

/**
 * @author steinerb
 */
public class VectorUtils {

    public static Vector3 mirror(ImmutableVector3 direction, ImmutableVector3 normal) {
        //R = 2 * N * dot(N, OV) - OV;
        float projectedLength = normal.dot(direction);
        return normal.mult(2.0f * projectedLength).minus(direction);
    }
}
