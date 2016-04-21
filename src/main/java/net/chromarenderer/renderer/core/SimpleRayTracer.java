package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.canvas.ChromaCanvas;
import net.chromarenderer.main.ChromaStatistics;
import net.chromarenderer.renderer.scene.ChromaScene;

/**
 * @author steinerb
 */
public class SimpleRayTracer extends ChromaCanvas implements Renderer {

    private final ChromaSettings settings;
    private final ChromaScene scene;
    private final Camera camera;
    private boolean completed;


    public SimpleRayTracer(ChromaSettings settings, ChromaScene scene, Camera camera) {
        super(settings.getImgWidth(), settings.getImgHeight());
        this.settings = settings;
        this.scene = scene;
        this.camera = camera;
        completed = false;
    }


    @Override
    public void renderNextImage(int imgWidth, int imgHeight, int widthOffset, int heightOffset) {
        if (!completed) {
            for (int j = heightOffset; j < imgHeight; j += 1) {
                for (int i = widthOffset; i < imgWidth; i += 1) {
                    ChromaThreadContext.setX(i);
                    ChromaThreadContext.setY(j);

                    // create camera/eye ray
                    Ray cameraRay = camera.getRay(i, j);

                    // scene intersection
                    Hitpoint hitpoint = scene.intersect(cameraRay);
                    ChromaStatistics.ray();

                    // very basic shading
                    Vector3 color = COLORS.BLACK;
                    if (hitpoint.hit()) {
                        color = hitpoint.getHitpointNormal().abs();
                    }

                    // set pixel value
                    pixels[width * j + i].set(color);
                }
            }

            completed = !isContinuous();
        }
    }


    @Override
    public boolean isContinuous() {
        return settings.isForceContinuousRender();
    }


    @Override
    public void flush() {
        flushCanvas();
    }


    @Override
    public byte[] get8BitRgbSnapshot() {
        return to8BitImage();
    }

}
