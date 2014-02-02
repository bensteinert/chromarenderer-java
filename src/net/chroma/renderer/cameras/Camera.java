package net.chroma.renderer.cameras;

import net.chroma.math.raytracing.Ray;

/**
 * @author steinerb
 */
public interface Camera {

    Ray getRay(int x, int y);
}
