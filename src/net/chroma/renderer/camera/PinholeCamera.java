package net.chroma.renderer.camera;

import net.chroma.math.ImmutableVector3;
import net.chroma.math.Vector3;
import net.chroma.math.raytracing.Ray;

/**
 * @author steinerb
 */
public class PinholeCamera implements Camera {

    private final int shiftX;
    private final int shiftY;
    Vector3 position;
    float focalDistance;

    float pixelSizeX;
    float pixelSizeY;

    public PinholeCamera(Vector3 position, float focalDistance, float pixelSizeX, float pixelSizeY, int imgWidth, int imgHeight) {
        this.position = position;
        this.focalDistance = focalDistance;
        this.pixelSizeX = pixelSizeX;
        this.pixelSizeY = pixelSizeY;
        this.shiftX = imgWidth / 2;
        this.shiftY = imgHeight / 2;

    }

    @Override
    public Ray getRay(int x, int y){
        // No rotation yet...
        return new Ray(
                new ImmutableVector3(position),
                new ImmutableVector3((x - shiftX) * pixelSizeX, (y - shiftY) * pixelSizeY, -focalDistance).normalize(),
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