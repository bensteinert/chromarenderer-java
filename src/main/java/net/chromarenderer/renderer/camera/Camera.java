package net.chromarenderer.renderer.camera;

import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;

/**
 * @author steinerb
 */

/**
 * The Camera encapsulates the logic of creating primary reyes starting from the image plane. Obviously, the image plane
 * (canvas) should also be part of that object, but from an engineering point of view it makes more sense to have the
 * pixel data inside the renderers.
 */
public interface Camera {

    /**
     * Produces rays which can be used for scene sampling. To say in other words, this method creates paths,
     * leaving the camera, originating the sensor.
     ***/
    Ray getRay(int x, int y);

    void move(Vector3 mutableVector3, Vector3 rotation);

    ImmutableVector3 getPosition();

    ImmutableMatrix3x3 getCoordinateSystem();

    float getFocalDistance();

    float getPixelSizeX();

    float getPixelSizeY();

    void resetToInitial();

    void recalibrateSensor(int newWidth, int newHeight);
}
