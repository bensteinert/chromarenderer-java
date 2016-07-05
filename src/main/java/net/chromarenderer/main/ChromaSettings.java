package net.chromarenderer.main;

import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.scene.SceneType;
import net.chromarenderer.renderer.scene.acc.AccStructType;

import java.nio.file.Path;


public class ChromaSettings {

    private final boolean parallelized;
    private final int imgWidth;
    private final int imgHeight;
    private final ChromaRenderMode renderMode;
    private final boolean lightSourceSamplingMode;
    private final AccStructType accStructType;
    private final SceneType sceneType;
    private final Path scenePath;
    // TODO make configurable
    private final int maxRayDepth = 9;

    // non-invasive properties
    private boolean computeL1 = false;


    public ChromaSettings(boolean parallelize, int imgWidth, int imgHeight, ChromaRenderMode renderMode, boolean lightSourceSamplingMode, AccStructType accStructType, SceneType sceneType, Path scenePath) {
        this.parallelized = parallelize;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.renderMode = renderMode;
        this.lightSourceSamplingMode = lightSourceSamplingMode;
        this.accStructType = accStructType;
        this.sceneType = sceneType;
        this.scenePath = scenePath;
    }


    public ChromaSettings(ChromaSettings settings) {
        this.parallelized = settings.parallelized;
        this.imgWidth = settings.getImgWidth();
        this.imgHeight = settings.getImgHeight();
        this.renderMode = settings.getRenderMode();
        this.lightSourceSamplingMode = settings.lightSourceSamplingMode;
        this.accStructType = settings.accStructType;
        this.scenePath = settings.scenePath;
        this.sceneType = settings.sceneType;
    }


    public int getImgWidth() {
        return imgWidth;
    }


    public int getImgHeight() {
        return imgHeight;
    }


    public ChromaRenderMode getRenderMode() {
        return renderMode;
    }


    public int getMaxRayDepth() {
        return maxRayDepth;
    }


    public boolean isDirectLightEstimationEnabled() {
        return lightSourceSamplingMode;
    }


    public AccStructType getAccStructType() {
        return accStructType;
    }


    public boolean isMultiThreaded() {
        return parallelized;
    }


    public boolean computeL1Norm() {
        return computeL1;
    }


    public Path getScenePath() {
        return scenePath;
    }


    public SceneType getSceneType() {
        return sceneType;
    }


    public void toggleL1Computation() {
        computeL1 = !computeL1;
    }


    public String getSceneName() {
        switch (sceneType) {
            case BLENDER_EXPORT:
                return scenePath.getFileName().toString();
            case CORNELL_BOX:
                return "builtInCornell";
            case FURNACE_TEST:
                return "furnaceTest";
            default:
                return "screenshot";
        }
    }
}
