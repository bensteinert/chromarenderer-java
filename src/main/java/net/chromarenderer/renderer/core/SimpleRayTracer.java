package net.chromarenderer.renderer.core;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.MutableVector3;
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

    private final GeometryScene scene;
    private boolean completed;
    private final Camera camera;
    private final ChromaStatistics statistics;


    public SimpleRayTracer(int imageWidth, int imageHeight, GeometryScene scene, Camera camera, ChromaStatistics statistics) {
        super(imageWidth, imageHeight);
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

                    Vector3 color = COLORS.BLACK;

                    // shading
                    if (hitpoint.hit()) {
                        Radiance radiance = scene.getRadianceSample(hitpoint);
                        color = radiance.getColor();
                    }

                    pixels[width * j + i] = new MutableVector3(color);
                }
            }
            completed = !isContinuous();
        }
    }



    @Override
    public boolean isContinuous() {
        return true;
    }

    @Override
    public byte[] get8BitRgbSnapshot() {
        return to8BitImage();
    }

}
