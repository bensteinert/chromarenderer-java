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

import java.util.stream.IntStream;

/**
 * @author steinerb
 */
public class SimpleRayCaster extends ChromaCanvas implements Renderer {

    private final ChromaSettings settings;
    private final ChromaScene scene;
    private final Camera camera;


    public SimpleRayCaster(ChromaSettings settings, ChromaScene scene, Camera camera) {
        super(settings.getImgWidth(), settings.getImgHeight());
        this.settings = settings;
        this.scene = scene;
        this.camera = camera;
    }


    @Override
    public void renderNextImage() {
        if (settings.parallelized()){
            IntStream.range(0, settings.getImgHeight()).parallel().forEach(j ->
                    IntStream.range(0, settings.getImgWidth()).parallel().forEach(i -> {
                        renderPixel(j, i);
                    })
            );
        }
        else {
            for (int j = 0; j < settings.getImgHeight(); j += 1) {
                for (int i = 0; i < settings.getImgWidth(); i += 1) {
                    renderPixel(j, i);
                }
            }
        }
    }


    private void renderPixel(int j, int i) {
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


    @Override
    public void flush() {
        flushCanvas();
    }


    @Override
    public byte[] get8BitRgbSnapshot() {
        return to8BitImage();
    }

}
