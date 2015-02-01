package net.chromarenderer.renderer.camera;

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

    public static final ImmutableVector3 MOVER_Z = new ImmutableVector3(0.0f, 0.0f, -0.1f);
    public static final ImmutableVector3 MOVER_X = new ImmutableVector3(-0.1f, 0.0f, 0.0f);
    public static final ImmutableVector3 MOVER_Y = new ImmutableVector3(0.0f, 0.1f, 0.0f);

    Ray getRay(int x, int y);

    void move(Vector3 mutableVector3);
}
