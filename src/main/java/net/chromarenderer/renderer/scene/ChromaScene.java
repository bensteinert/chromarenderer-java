package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;

/**
 * @author bensteinert
 */
public interface ChromaScene {
    Hitpoint intersect(Ray cameraRay);

    Hitpoint getLightSourceSample();

    boolean isObstructed(Ray shadowRay);
}
