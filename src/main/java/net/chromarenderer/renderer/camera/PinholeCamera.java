package net.chromarenderer.renderer.camera;

import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import org.apache.commons.math3.util.FastMath;

/**
 * @author bensteinert
 */
public class PinholeCamera implements Camera {

    private final ImmutableVector3 initialPosition;
    private ImmutableMatrix3x3 initialCoordinateSystem;

    private ImmutableVector3 position;
    private ImmutableMatrix3x3 coordinateSystem;

    private float focalDistance;
    private float pixelSizeX;
    private float pixelSizeY;
    private float shiftX;
    private float shiftY;


    public PinholeCamera(ImmutableVector3 position, float focalDistance, float pixelSizeX, float pixelSizeY, int pixelsX, int pixelsY) {
        this(position, new ImmutableMatrix3x3(Vector3.X_AXIS, Vector3.Y_AXIS, Vector3.Z_AXIS), focalDistance, pixelSizeX, pixelSizeY, pixelsX, pixelsY);
    }


    public PinholeCamera(ImmutableVector3 position, ImmutableMatrix3x3 coordinateSystem, float focalDistance, float pixelSizeX, float pixelSizeY, int pixelsX, int pixelsY) {
        this.initialPosition = position;
        this.position = position;
        this.initialCoordinateSystem = coordinateSystem;
        this.coordinateSystem = coordinateSystem;
        this.focalDistance = focalDistance;
        this.pixelSizeX = pixelSizeX;
        this.pixelSizeY = pixelSizeY;
        this.shiftX = (pixelsX / 2) * pixelSizeX;
        this.shiftY = (pixelsY / 2) * pixelSizeY;
    }


    public PinholeCamera(ImmutableVector3 position, int pixelsX, int pixelsY) {
        this(position, 50.0f, 36.0f / pixelsX, (36.0f * (pixelsY/pixelsX)) / pixelsY, pixelsX, pixelsY);
    }

    public static PinholeCamera createWithDefaults(){
        return new PinholeCamera(new ImmutableVector3(1, 1, 5), 1000, 1000);
    }


    @Override
    public Ray getRay(int x, int y) {
        float subSampleX = ChromaThreadContext.randomFloatClosedOpen();
        float subSampleY = ChromaThreadContext.randomFloatClosedOpen();

        ImmutableVector3 direction = new ImmutableVector3(((x + subSampleX) * pixelSizeX) - shiftX, ((y + subSampleY) * pixelSizeY) - shiftY, -focalDistance).normalize();
        return new Ray(new ImmutableVector3(position), coordinateSystem.mult(direction), 0, Float.MAX_VALUE, false);
    }


    @Override
    public void move(Vector3 translation, Vector3 rotationVector) {
        position = position.plus(coordinateSystem.mult(translation));

        float sin_t = (float) FastMath.sin(rotationVector.getY());
        float cos_t = (float) FastMath.cos(rotationVector.getY());
        float sin_p = (float) FastMath.sin(rotationVector.getX());
        float cos_p = (float) FastMath.cos(rotationVector.getX());

        ImmutableMatrix3x3 rotation = new ImmutableMatrix3x3(
                cos_p, 0.0f, sin_p,
                sin_t * sin_p, cos_t, -1.0f * sin_t * cos_p,
                -sin_p * cos_t, sin_t, cos_t * cos_p);

        this.coordinateSystem = rotation.mult(coordinateSystem).normalizeCols();
    }


    @Override
    public ImmutableVector3 getPosition() {
        return position;
    }


    @Override
    public ImmutableMatrix3x3 getCoordinateSystem() {
        return coordinateSystem;
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
        coordinateSystem = initialCoordinateSystem;
    }


    @Override
    public void recalibrateSensor(int newWidth, int newHeight) {
        //TODO: Add some sort of focal length compensation in order to keep field of view.
        this.shiftX = (newWidth / 2) * pixelSizeX;
        this.shiftY = (newHeight / 2) * pixelSizeY;
    }

}