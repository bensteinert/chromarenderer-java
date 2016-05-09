package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.main.ChromaStatistics;
import net.chromarenderer.math.Constants;
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
        if (settings.computeL1Norm()) {
            ChromaStatistics.L1Norm = buffer.computeL1();
        }
    }


    private void renderPixel(int j, int i) {
        ChromaThreadContext.setX(i);
        ChromaThreadContext.setY(j);
        Ray cameraRay = camera.getRay(i, j);
        pixels[width * j + i].set(kernel(cameraRay).getColor());
    }


    private Radiance kernel(Ray incomingRay) {
        int depth = 0;
        MutableVector3 result = new MutableVector3();
        MutableVector3 pathWeight = new MutableVector3(1.f, 1.f, 1.f);
        Hitpoint hitpoint = null;
        Radiance fr = null;
        if (settings.isDirectLightEstimationEnabled()) {
            hitpoint = scene.intersect(incomingRay);
            if (hitpoint.hit()) {
                // Add Le
                Material emitting = hitpoint.getHitGeometry().getMaterial();

                if (MaterialType.EMITTING.equals(emitting.getType())){
                    result.plus(emitting.getEmittance());
                }

                Radiance frDL = ShaderEngine.getDirectRadiance(incomingRay, hitpoint);
                if (frDL.getColor().getMaxValue() > Constants.FLT_EPSILON) {
                    result.plus(frDL.getColor());
                }

                fr = ShaderEngine.brdf2(hitpoint, incomingRay);
                pathWeight = pathWeight.mult(fr.getColor());
                incomingRay = fr.getLightRay();

                while (pathWeight.getMaxValue() > Constants.FLT_EPSILON && depth < settings.getMaxRayDepth()) {
                    hitpoint = scene.intersect(incomingRay);
                    depth++;
                    if (hitpoint.hit()) {
                        frDL = ShaderEngine.getDirectRadiance(incomingRay, hitpoint);
                        //System.out.println("frDL" + frDL);
                        if (frDL.getColor().getMaxValue() > Constants.FLT_EPSILON) {
                            result.plus(frDL.getColor().mult(pathWeight));
                        }

                        fr = ShaderEngine.brdf2(hitpoint, incomingRay);
                        pathWeight = pathWeight.mult(russianRoulette()).mult(fr.getColor());
                        incomingRay = fr.getLightRay();
                    }
                    else break;
                }
            }
        }
        else {
            // L = Le + ∫ fr * Li
            while (pathWeight.getMaxValue() > Constants.FLT_EPSILON && depth < settings.getMaxRayDepth()) {
                // scene intersection
                hitpoint = scene.intersect(incomingRay);
                depth++;

                if (hitpoint.hit()) {
                    // Add Le
                    Material emitting = hitpoint.getHitGeometry().getMaterial();
                    if (MaterialType.EMITTING.equals(emitting.getType())) {
                        result.plus(emitting.getEmittance().mult(pathWeight));
                    }

                    fr = ShaderEngine.brdf2(hitpoint, incomingRay);
                    pathWeight = pathWeight.mult(russianRoulette()).mult(fr.getColor());
                    incomingRay = fr.getLightRay();
                }
            }
        }
        return new Radiance(result, null);
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
