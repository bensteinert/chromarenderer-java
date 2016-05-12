package net.chromarenderer.main;

import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.scene.acc.AccStructType;


public class ChromaSettings {

    private final boolean parallelized;
    private final int imgWidth;
    private final int imgHeight;
    private final ChromaRenderMode renderMode;
    private final boolean lightSourceSamplingMode;
    private final AccStructType accStructType;
    // TODO make configurable
    private final int maxRayDepth = 20;

    private boolean computeL1 = false;


    public ChromaSettings(boolean parallelize, int imgWidth, int imgHeight, ChromaRenderMode renderMode, boolean lightSourceSamplingMode, AccStructType accStructType) {
        this.parallelized = parallelize;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.renderMode = renderMode;
        this.lightSourceSamplingMode = lightSourceSamplingMode;
        this.accStructType = accStructType;
    }


    public ChromaSettings(ChromaSettings settings) {
        this.parallelized = settings.parallelized;
        this.imgWidth = settings.getImgWidth();
        this.imgHeight = settings.getImgHeight();
        this.renderMode = settings.getRenderMode();
        this.lightSourceSamplingMode = settings.lightSourceSamplingMode;
        this.accStructType = settings.accStructType;
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


    public void toggleL1Computation() {
        computeL1 = !computeL1;
    }
}
