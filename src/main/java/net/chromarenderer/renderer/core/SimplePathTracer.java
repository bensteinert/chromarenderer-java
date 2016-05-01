package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.main.ChromaStatistics;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.shader.ShaderEngine;
import net.chromarenderer.renderer.RecursiveRenderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.canvas.AccumulationBuffer;
import net.chromarenderer.renderer.canvas.ChromaCanvas;
import net.chromarenderer.renderer.canvas.ParallelStreamAccumulationBuffer;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;

import java.util.stream.IntStream;

/**
 * @author bensteinert
 */
public class SimplePathTracer extends ChromaCanvas implements RecursiveRenderer {

    private final AccumulationBuffer buffer;
    private final ChromaSettings settings;
    private final ChromaScene scene;
    private final Camera camera;


    public SimplePathTracer(ChromaSettings settings, ChromaScene scene, Camera camera) {
        super(settings.getImgWidth(), settings.getImgHeight());
        this.settings = settings;
        this.scene = scene;
        this.camera = camera;
        buffer = new ParallelStreamAccumulationBuffer(settings.getImgWidth(), settings.getImgHeight());
    }


    @Override
    public void renderNextImage() {
        if (settings.isMultiThreaded()) {
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

        buffer.accumulate(getPixels());
    }


    private void renderPixel(int j, int i) {
        ChromaThreadContext.setX(i);
        ChromaThreadContext.setY(j);
        Ray cameraRay = camera.getRay(i, j);
        pixels[width * j + i].set(recursiveKernel(cameraRay, 0, 1.0f).getColor());
    }


    public Radiance recursiveKernel(Ray incomingRay, int depth, float pathWeight) {
        // scene intersection
        Hitpoint hitpoint = scene.intersect(incomingRay);
        ChromaStatistics.ray();

        // shading
        Vector3 color = COLORS.BLACK;
        if (hitpoint.hit()) {
            Radiance directRadianceSample = ShaderEngine.getDirectRadianceSample(incomingRay, hitpoint, pathWeight, settings);

            if (settings.getMaxRayDepth() > depth && pathWeight > Constants.FLT_EPSILON) {
                Radiance indirectRadianceSample = ShaderEngine.getIndirectRadianceSample(incomingRay, hitpoint, this, depth, pathWeight);
                color = ShaderEngine.brdf(hitpoint, directRadianceSample, indirectRadianceSample);
            }
        }

        return new Radiance(color, incomingRay);
    }


    @Override
    public void flush() {
        flushCanvas();
        buffer.flushBuffer();
    }


    @Override
    public byte[] get8BitRgbSnapshot() {
        return buffer.to8BitImage();
    }

}
