package net.chromarenderer.main;

import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.scene.acc.AccStructType;


public class ChromaSettings {

    private final boolean parallelized;
    private final int imgWidth;
    private final int imgHeight;
    private final ChromaRenderMode renderMode;
    @Deprecated
    private final boolean forceContinuousRender;
    private final int maxRayDepth = 3;
    private final boolean lightSourceSamplingMode;
    private final AccStructType accStructType;


    public ChromaSettings(boolean parallelized, int imgWidth, int imgHeight, ChromaRenderMode renderMode, boolean forceContinuousRender, boolean lightSourceSamplingMode, AccStructType accStructType) {
        this.parallelized = parallelized;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.renderMode = renderMode;
        this.forceContinuousRender = forceContinuousRender;
        this.lightSourceSamplingMode = lightSourceSamplingMode;
        this.accStructType = accStructType;
    }


    public ChromaSettings(ChromaSettings settings) {
        this.parallelized = settings.parallelized;
        this.imgWidth = settings.getImgWidth();
        this.imgHeight = settings.getImgHeight();
        this.renderMode = settings.getRenderMode();
        this.forceContinuousRender = settings.isForceContinuousRender();
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


    public boolean isForceContinuousRender() {
        return forceContinuousRender;
    }


    public ChromaSettings changeMode(ChromaRenderMode mode) {
        return new ChromaSettings(parallelized, imgWidth, imgHeight, mode, forceContinuousRender, lightSourceSamplingMode, accStructType);
    }


    public ChromaSettings changeContinuousRender(boolean isContinuousRender) {
        return new ChromaSettings(parallelized, imgWidth, imgHeight, renderMode, isContinuousRender, lightSourceSamplingMode, accStructType);

    }

    public int getMaxRayDepth() {
        return maxRayDepth;
    }


    public ChromaSettings changeDirectLightEstimation(Boolean value) {
        return new ChromaSettings(parallelized, imgWidth, imgHeight, renderMode, forceContinuousRender, value, accStructType);
    }

    public ChromaSettings changeDoParallelize(Boolean value) {
        return new ChromaSettings(value, imgWidth, imgHeight, renderMode, forceContinuousRender, value, accStructType);
    }


    public boolean isDirectLightEstimationEnabled(){
        return lightSourceSamplingMode;
    }


    public AccStructType getAccStructType() {
        return accStructType;
    }


    public ChromaSettings changeAccStructMode(AccStructType newValue) {
        return new ChromaSettings(parallelized, imgWidth, imgHeight, renderMode, forceContinuousRender, lightSourceSamplingMode, newValue);
    }


    public ChromaSettings changeResolution(int width, int height) {
        return new ChromaSettings(parallelized, width, height, renderMode, forceContinuousRender, lightSourceSamplingMode, accStructType);
    }


    public boolean parallelized() {
        return parallelized;
    }
}
