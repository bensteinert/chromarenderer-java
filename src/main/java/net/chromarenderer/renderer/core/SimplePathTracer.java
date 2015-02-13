package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.math.shader.ShaderEngine;
import net.chromarenderer.renderer.RecursiveRenderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.canvas.AccumulationBuffer;
import net.chromarenderer.renderer.canvas.ChromaCanvas;
import net.chromarenderer.renderer.canvas.SingleThreadedAccumulationBuffer;
import net.chromarenderer.renderer.diag.ChromaStatistics;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author steinerb
 */
public class SimplePathTracer extends ChromaCanvas implements RecursiveRenderer {

    private final AccumulationBuffer buffer;
    private final ChromaSettings settings;
    private final GeometryScene scene;
    private boolean completed;
    private final Camera camera;
    private final ChromaStatistics statistics;


    public SimplePathTracer(ChromaSettings settings, GeometryScene scene, Camera camera, ChromaStatistics statistics) {
        super(settings.getImgWidth(), settings.getImgHeight());
        this.settings = settings;
        this.scene = scene;
        this.camera = camera;
        this.statistics = statistics;
        completed = false;
        buffer = new SingleThreadedAccumulationBuffer(settings.getImgWidth(), settings.getImgHeight());
    }


    @Override
    public void renderNextImage(int imgWidth, int imgHeight, int widthOffset, int heightOffset) {

        if (!completed) {
            for (int j = heightOffset; j < imgHeight; j += 1) {
                for (int i = widthOffset; i < imgWidth; i += 1) {
                    ChromaThreadContext.setX(i);
                    ChromaThreadContext.setY(j);
                    Ray cameraRay = camera.getRay(i, j);
                    pixels[width * j + i].set(recursiveKernel(cameraRay, 0, 1.0f).getColor());
                }
            }
            //TODO ConcurrencyIssue: That will cause trouble when fork/joining image!
            buffer.accumulate(getPixels());
            completed = !isContinuous();
        }
    }


    public Radiance recursiveKernel(Ray incomingRay, int depth, float pathWeight) {
        // scene intersection
        Hitpoint hitpoint = scene.intersect(incomingRay);
        statistics.ray();

        // shading
        Vector3 color = COLORS.BLACK;
        if (hitpoint.hit()) {
            Radiance directRadianceSample = ShaderEngine.getDirectRadianceSample(incomingRay, hitpoint, pathWeight);

            if (settings.getMaxRayDepth() > depth && pathWeight > Constants.FLT_EPSILON) {
                Radiance indirectRadianceSample = ShaderEngine.getIndirectRadianceSample(incomingRay, hitpoint, this, depth, pathWeight);
                color = ShaderEngine.brdf(hitpoint, directRadianceSample, indirectRadianceSample);
            }
        }

        return new Radiance(color, incomingRay);
    }


    @Override
    public boolean isContinuous() {
        return true;
    }


    @Override
    public byte[] get8BitRgbSnapshot() {
        return buffer.to8BitImage();
    }

}
