package net.chromarenderer.math;

/**
 * @author steinerb
 */
public class ChromaFloat {

    public static float rad(float deg) {
        return deg / 180.0f * Constants.PI_f;
    }

    public static float deg(float radian) {
        return radian * 180.0f / Constants.PI_f;
    }
}
