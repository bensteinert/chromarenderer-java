package net.chromarenderer.main;

import net.chromarenderer.renderer.ChromaRenderMode;

/**
 * @author steinerb
 */
public class ChromaSettings {

    private int imgWidth = 1024;
    private int imgHeight = 1024;
    private ChromaRenderMode mode = ChromaRenderMode.SIMPLE;
    private boolean forceContinuousRender = false;


    public ChromaSettings() {
    }


    public ChromaSettings(ChromaSettings settings) {
        this.imgWidth = settings.getImgWidth();
        this.imgHeight = settings.getImgHeight();
        this.mode = settings.getMode();
        this.forceContinuousRender = settings.isForceContinuousRender();
    }


    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }


    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }


    public int getImgWidth() {
        return imgWidth;
    }


    public int getImgHeight() {
        return imgHeight;
    }


    public ChromaRenderMode getMode() {
        return mode;
    }


    public void setMode(ChromaRenderMode mode) {
        this.mode = mode;
    }


    public boolean isForceContinuousRender() {
        return forceContinuousRender;
    }


    public void setForceContinuousRender(boolean forceContinuousRender) {
        this.forceContinuousRender = forceContinuousRender;
    }
}
