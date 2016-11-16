package net.chromarenderer.renderer.camera;

import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.LensTracer;
import net.chromarenderer.renderer.scene.acc.LensSystem;

/**
 * @author bensteinert
 */
//TODO: Should also implement Geometry in order to participate in scene sampling with the front lens (important for Light Tracing)
public class RealLensCamera implements Camera {

    // TODO: All identical with PinholeCamera so far. Extract base class
    private final ImmutableVector3 initialPosition;
    private ImmutableMatrix3x3 initialCoordinateSystem;

    private ImmutableVector3 position;
    private ImmutableMatrix3x3 coordinateSystem;

    private float focalDistance;
    private float pixelSizeX;
    private float pixelSizeY;
    private float shiftX;
    private float shiftY;

    // new
    private final LensSystem lensSystem;
    private final LensTracer lensTracer;

    @Override
    public Ray getRay(int x, int y) {
        // Sample pixel, start ray and send it through the LensSystem
        return null;
    }

    @Override
    public void move(Vector3 mutableVector3, Vector3 rotation) {

    }

    @Override
    public ImmutableVector3 getPosition() {
        return null;
    }

    @Override
    public ImmutableMatrix3x3 getCoordinateSystem() {
        return null;
    }

    @Override
    public float getFocalDistance() {
        return 0;
    }

    @Override
    public float getPixelSizeX() {
        return 0;
    }

    @Override
    public float getPixelSizeY() {
        return 0;
    }

    @Override
    public void resetToInitial() {

    }

    @Override
    public void recalibrateSensor(int newWidth, int newHeight) {

    }
}
