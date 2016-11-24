package net.chromarenderer.math;

import org.apache.commons.math3.util.FastMath;

/**
 * @author steinerb
 */
public class ChromaFloat {

    public static boolean approxEqual(float a, float b) {
        return approxEqual(a,b, 1);
    }

    public static boolean approxEqual(float a, float b, int epsilonShift) {
        return FastMath.abs(a - b) < Constants.FLT_EPSILON * epsilonShift;
    }

    public static float rad(float deg) {
        return deg / 180.0f * Constants.PI_f;
    }

    public static float deg(float radian) {
        return radian * 180.0f / Constants.PI_f;
    }
}
