package net.chromarenderer.math;

import net.chromarenderer.math.raytracing.CoordinateSystem;
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

    public static CoordinateSystem buildCoordSystem(ImmutableVector3 vector){
        MutableVector3 t1 = new MutableVector3(vector);
        t1.setValue(t1.getMinIndexAbs(), 1.0f);
        t1.crossProduct(vector);
        t1.normalize();
        ImmutableVector3 t2 = vector.crossProduct(t1).normalize();
        return new CoordinateSystem(new ImmutableVector3(t1), t2, vector);
    }

    public static ImmutableVector3 computeNormal(ImmutableVector3 p0, ImmutableVector3 p1, ImmutableVector3 p2){
        return (p1.minus(p0).crossProduct(p2.minus(p0))).normalize();
    }
}
