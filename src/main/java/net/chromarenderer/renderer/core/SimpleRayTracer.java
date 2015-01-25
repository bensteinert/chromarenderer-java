package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.canvas.ChromaCanvas;
import net.chromarenderer.renderer.diag.ChromaStatistics;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author steinerb
 */
public class SimpleRayTracer extends ChromaCanvas implements Renderer {

    private final ChromaSettings settings;
    private final GeometryScene scene;
    private boolean completed;
    private final Camera camera;
    private final ChromaStatistics statistics;


    public SimpleRayTracer(ChromaSettings settings, GeometryScene scene, Camera camera, ChromaStatistics statistics) {
        super(settings.getImgWidth(), settings.getImgHeight());
        this.settings = settings;
        this.scene = scene;
        this.camera = camera;
        this.statistics = statistics;
        completed = false;
    }


    @Override
    public void renderNextImage(int imgWidth, int imgHeight, int widthOffset, int heightOffset) {
        if (!completed) {
            for (int j = heightOffset; j < imgHeight; j += 1) {
                for (int i = widthOffset; i < imgWidth; i += 1) {
                    ChromaThreadContext.setX(i);
                    ChromaThreadContext.setY(j);
                    Ray cameraRay = camera.getRay(i, j);

                    // scene intersection
                    Hitpoint hitpoint = scene.intersect(cameraRay);


                    // shading
                    Vector3 color = COLORS.BLACK;
                    if (hitpoint.hit()) {
                        Radiance radiance = ShaderEngine.getDirectRadianceSample(hitpoint);
                        color = radiance.getColor();
                    }

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
    public byte[] get8BitRgbSnapshot() {
        return to8BitImage();
    }

}
