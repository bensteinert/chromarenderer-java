package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.camera.CoreCamera;
import net.chromarenderer.renderer.camera.PinholeCamera;

/**
 * @author bensteinert
 */
public class EmptyScene implements ChromaScene {

    private final CoreCamera camera;

    private EmptyScene(PinholeCamera camera) {
        this.camera = camera;
    }

    public static EmptyScene create() {
        return new EmptyScene(PinholeCamera.createWithDefaults());
    }

    @Override
    public Hitpoint intersect(Ray cameraRay) {
        return Hitpoint.INFINITY;
    }

    @Override
    public Hitpoint getLightSourceSample() {
        return Hitpoint.INFINITY;
    }

    @Override
    public boolean isObstructed(Ray shadowRay) {
        return false;
    }

    @Override
    public int getNumberOfLightSources() {
        return 0;
    }

    @Override
    public CoreCamera getCamera() {
        return camera;
    }
}
