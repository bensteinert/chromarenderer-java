package net.chromarenderer.main;

import net.chromarenderer.renderer.ChromaRenderMode;


public class ChromaSettings {

    private final int threadCount;
    private final int imgWidth;
    private final int imgHeight;
    private final ChromaRenderMode renderMode;
    private final boolean forceContinuousRender;
    private final int maxRayDepth = 3;
    private final boolean lightSourceSamplingMode;


    public ChromaSettings(int imgWidth, int imgHeight, ChromaRenderMode renderMode, boolean forceContinuousRender, int threadCount, boolean lightSourceSamplingMode) {
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.renderMode = renderMode;
        this.forceContinuousRender = forceContinuousRender;
        this.threadCount = threadCount;
        this.lightSourceSamplingMode = lightSourceSamplingMode;
    }


    public ChromaSettings(ChromaSettings settings) {
        this.imgWidth = settings.getImgWidth();
        this.imgHeight = settings.getImgHeight();
        this.renderMode = settings.getRenderMode();
        this.forceContinuousRender = settings.isForceContinuousRender();
        this.threadCount = settings.threadCount;
        this.lightSourceSamplingMode = settings.lightSourceSamplingMode;
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
        return new ChromaSettings(imgWidth, imgHeight, mode, forceContinuousRender, threadCount, lightSourceSamplingMode);
    }


    public ChromaSettings changeContinuousRender(boolean isContinuousRender) {
        return new ChromaSettings(imgWidth, imgHeight, renderMode, isContinuousRender, threadCount, lightSourceSamplingMode);

    }

    public int getMaxRayDepth() {
        return maxRayDepth;
    }


    public int getThreadCount() {
        return threadCount;
    }


    public ChromaSettings toggleLightSourceSamplingMode() {
        return new ChromaSettings(imgWidth, imgHeight, renderMode, forceContinuousRender, threadCount, !lightSourceSamplingMode);
    }

    public boolean isLightSourceSamplingEnabled(){
        return lightSourceSamplingMode;
    }
}
