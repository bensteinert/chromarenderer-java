package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.canvas.AccumulationBuffer;
import net.chromarenderer.renderer.canvas.ChromaCanvas;
import net.chromarenderer.renderer.canvas.ParallelStreamAccumulationBuffer;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;
import net.chromarenderer.renderer.shader.Material;
import net.chromarenderer.renderer.shader.MaterialType;
import net.chromarenderer.renderer.shader.ShaderEngine;

import java.util.stream.IntStream;

/**
 * @author bensteinert
 */
public class MonteCarloPathTracer extends ChromaCanvas implements Renderer {

    private final AccumulationBuffer buffer;
    private final ChromaSettings settings;
    private final ChromaScene scene;
    private final Camera camera;


    public MonteCarloPathTracer(ChromaSettings settings, ChromaScene scene, Camera camera) {
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
        pixels[width * j + i].set(kernel(cameraRay).getColor());
    }


    public Radiance kernel(Ray incomingRay) {
        float pathWeight = 1.0f;
        int depth = 0;
        MutableVector3 result = new MutableVector3();

        // L = Le + ∫ fr * Li
        while (pathWeight > Constants.FLT_EPSILON && depth < settings.getMaxRayDepth()) {
            // scene intersection
            Hitpoint hitpoint = scene.intersect(incomingRay);
            depth++;

            if (hitpoint.hit()) {
                // Add Le
                Material emitting = hitpoint.getHitGeometry().getMaterial();
                if (MaterialType.EMITTING.equals(emitting.getType())){
                    result.plus(emitting.getEmittance().mult(pathWeight));
                }

                ImmutableVector3 fr = ShaderEngine.brdf2(hitpoint, incomingRay);
                pathWeight = pathWeight * russianRoulette() * fr.getMaxValue();
            }

        }

        return new Radiance(result, incomingRay);
    }


    private float russianRoulette() {
        float russianRoulette = ChromaThreadContext.randomFloatClosedOpen();
        return russianRoulette > Constants.RR_LIMIT ? 0.f : 1.0f/Constants.RR_LIMIT;
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
