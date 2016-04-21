package net.chromarenderer.renderer.camera;

import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;

/**
 * @author steinerb
 */
public class PinholeCamera implements Camera {

    private final ImmutableVector3 initialPosition;

    private ImmutableVector3 position;
    private ImmutableMatrix3x3 coordinateSystem;

    private float focalDistance;
    private float pixelSizeX;
    private float pixelSizeY;
    private float shiftX;
    private float shiftY;

    public PinholeCamera(ImmutableVector3 position, float focalDistance, float pixelSizeX, float pixelSizeY, int pixelsX, int pixelsY) {
        this.initialPosition = new ImmutableVector3(position);
        this.position = position;
        this.coordinateSystem = new ImmutableMatrix3x3(Vector3.X_AXIS, Vector3.Y_AXIS, Vector3.Z_AXIS);
        this.focalDistance = focalDistance;
        this.pixelSizeX = pixelSizeX;
        this.pixelSizeY = pixelSizeY;
        this.shiftX = (pixelsX / 2) * pixelSizeX;
        this.shiftY = (pixelsY / 2) * pixelSizeY;
    }

    @Override
    public Ray getRay(int x, int y){
        // No rotation yet...
        float subSampleX = ChromaThreadContext.randomFloatClosedOpen();
        float subSampleY = ChromaThreadContext.randomFloatClosedOpen();

        ImmutableVector3 direction = new ImmutableVector3(((x + subSampleX) * pixelSizeX) - shiftX, ((y + subSampleY) * pixelSizeY) - shiftY, -focalDistance).normalize();
        return new Ray(new ImmutableVector3(position), coordinateSystem.mult(direction), 0, Float.MAX_VALUE);
    }


    @Override
    public void move(Vector3 translation, Vector3 rotationVector) {
        position = position.plus(coordinateSystem.mult(translation));

        //SLEEF FastMath useful here probably!
        float sin_t = (float) Math.sin(rotationVector.getY());
        float cos_t = (float) Math.cos(rotationVector.getY());
        float sin_p = (float) Math.sin(rotationVector.getX());
        float cos_p = (float) Math.cos(rotationVector.getX());

        ImmutableMatrix3x3 rotation = new ImmutableMatrix3x3(cos_p,        0.0f,   sin_p,
                                                                       sin_t*sin_p,  cos_t, -1.0f*sin_t*cos_p,
                                                                       -sin_p*cos_t, sin_t, cos_t*cos_p);

        this.coordinateSystem = rotation.mult(coordinateSystem).normalizeCols();
    }

    @Override
    public ImmutableVector3 getPosition() {
        return position;
    }


    @Override
    public float getFocalDistance() {
        return focalDistance;
    }


    @Override
    public float getPixelSizeX() {
        return pixelSizeX;
    }


    @Override
    public float getPixelSizeY() {
        return pixelSizeY;
    }


    @Override
    public void resetToInitial() {
        position = initialPosition;
        this.coordinateSystem = new ImmutableMatrix3x3(Vector3.X_AXIS, Vector3.Y_AXIS, Vector3.Z_AXIS);
    }


    @Override
    public void recalibrateSensor(int newWidth, int newHeight) {
        this.shiftX = (newWidth / 2) * pixelSizeX;
        this.shiftY = (newHeight / 2) * pixelSizeY;
    }

}