package net.chromarenderer.renderer;

import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author steinerb
 */
public interface RecursiveRenderer extends Renderer {

    Radiance recursiveKernel(Ray incomingRay, int depth);
}
