package net.chromarenderer.renderer.camera;

import net.chromarenderer.Camera;
import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;

/**
 * The CoreCamera encapsulates the logic of creating primary reyes starting from the image plane. Obviously, the image plane
 * (canvas) should also be part of that object, but from an engineering point of view it makes more sense to have the
 * pixel data inside the renderers.
 *
 * @author bensteinert
 *
 */
public interface CoreCamera extends Camera {

    Ray getRay(int x, int y);

    void move(Vector3 mutableVector3, Vector3 rotation);

    ImmutableVector3 getPosition();

    ImmutableMatrix3x3 getCoordinateSystem();

    float getFocalDistance();

    float getPixelSizeX();

    float getPixelSizeY();

    void resetToInitial();

    void recalibrateSensor(int newWidth, int newHeight);


    default void move(float[] translation, float[] rotation){
        move(new ImmutableVector3(translation), new ImmutableVector3(rotation));
    }

    default String getPositionAsString() {
        return getPosition().toString();
    }

    default String getCoordinateSystemAsString() {
        return getCoordinateSystem().toString();
    }
}
