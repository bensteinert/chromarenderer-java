package net.chromarenderer.main;

import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.scene.acc.AccStructType;


public class ChromaSettings {

    private final int threadCount;
    private final int imgWidth;
    private final int imgHeight;
    private final ChromaRenderMode renderMode;
    private final boolean forceContinuousRender;
    private final int maxRayDepth = 3;
    private final boolean lightSourceSamplingMode;
    private final AccStructType accStructType;


    public ChromaSettings(int imgWidth, int imgHeight, ChromaRenderMode renderMode, boolean forceContinuousRender, int threadCount, boolean lightSourceSamplingMode, AccStructType accStructType) {
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.renderMode = renderMode;
        this.forceContinuousRender = forceContinuousRender;
        this.threadCount = threadCount;
        this.lightSourceSamplingMode = lightSourceSamplingMode;
        this.accStructType = accStructType;
    }


    public ChromaSettings(ChromaSettings settings) {
        this.imgWidth = settings.getImgWidth();
        this.imgHeight = settings.getImgHeight();
        this.renderMode = settings.getRenderMode();
        this.forceContinuousRender = settings.isForceContinuousRender();
        this.threadCount = settings.threadCount;
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
        return new ChromaSettings(imgWidth, imgHeight, mode, forceContinuousRender, threadCount, lightSourceSamplingMode, accStructType);
    }


    public ChromaSettings changeContinuousRender(boolean isContinuousRender) {
        return new ChromaSettings(imgWidth, imgHeight, renderMode, isContinuousRender, threadCount, lightSourceSamplingMode, accStructType);

    }

    public int getMaxRayDepth() {
        return maxRayDepth;
    }


    public int getThreadCount() {
        return threadCount;
    }


    public ChromaSettings changeDirectLightEstimation(Boolean value) {
        return new ChromaSettings(imgWidth, imgHeight, renderMode, forceContinuousRender, threadCount, value, accStructType);
    }

    public boolean isDirectLightEstimationEnabled(){
        return lightSourceSamplingMode;
    }


    public AccStructType getAccStructType() {
        return accStructType;
    }


    public ChromaSettings changeAccStructMode(AccStructType newValue) {
        return new ChromaSettings(imgWidth, imgHeight, renderMode, forceContinuousRender, threadCount, lightSourceSamplingMode, newValue);

    }


    public ChromaSettings changeResolution(int width, int height) {
        return new ChromaSettings(width, height, renderMode, forceContinuousRender, threadCount, lightSourceSamplingMode, accStructType);
    }
}
