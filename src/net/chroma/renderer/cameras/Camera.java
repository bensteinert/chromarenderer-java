package net.chroma.renderer.cameras;

import net.chroma.math.raytracing.Ray;

/**
 * @author steinerb
 */

/**
 * The Camera encapsulates the logic of creating primary reyes starting from the image plane. Obviously, the image plane
 * (canvas) should also be part of that object, but from an engineering point of view it makes more sense to have the
 * pixel data inside the renderers.
 */
public interface Camera {

    Ray getRay(int x, int y);
}
