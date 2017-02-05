package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.camera.CoreCamera;

/**
 * @author bensteinert
 */
public interface ChromaScene {

    Hitpoint intersect(Ray cameraRay);

    Hitpoint getLightSourceSample();

    boolean isObstructed(Ray shadowRay);

    int getNumberOfLightSources();

    CoreCamera getCamera();
}
