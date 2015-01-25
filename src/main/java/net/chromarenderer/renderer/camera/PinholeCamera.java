package net.chromarenderer.renderer.camera;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;

/**
 * @author steinerb
 */
public class PinholeCamera implements Camera {

    private final float shiftX;
    private final float shiftY;

    private Vector3 position;

    private float focalDistance;
    private float pixelSizeX;
    private float pixelSizeY;

    public PinholeCamera(Vector3 position, float focalDistance, float pixelSizeX, float pixelSizeY, int pixelsX, int pixelsY) {
        this.position = position;
        this.focalDistance = focalDistance;
        this.pixelSizeX = pixelSizeX;
        this.pixelSizeY = pixelSizeY;
        this.shiftX = (pixelsX / 2) * pixelSizeX;
        this.shiftY = (pixelsY / 2) * pixelSizeY;

    }

    @Override
    public Ray getRay(int x, int y){
        // No rotation yet...
        float subSampleX = ChromaThreadContext.randomFloat();
        float subSampleY = ChromaThreadContext.randomFloat();

        return new Ray(
                new ImmutableVector3(position),
                new ImmutableVector3(((x + subSampleX) * pixelSizeX) - shiftX, ((y + subSampleY) * pixelSizeY) - shiftY, -focalDistance).normalize(),
                0,
                Float.MAX_VALUE);
    }

    //taken from Chroma V1
//    public void rotate(int degX, int degY){
//
//        float sin_t = sin(RAD(degY));
//        float cos_t = cos(RAD(degY));
//        float sin_p = sin(RAD(degX));
//        float cos_p = cos(RAD(degX));
//
//        rot = rot * Matrix3x3(Vector3(cos_p, sin_t*sin_p, -sin_p*cos_t),Vector3(0,cos_t,sin_t),Vector3(sin_p,(-1.0)*sin_t*cos_p,cos_t*cos_p));;
//        rot[0].normalize();
//        rot[1].normalize();
//        rot[2].normalize();
//        inv_rot = rot.transpose(); // rot othonormal so it is ok ;)
//    }

}