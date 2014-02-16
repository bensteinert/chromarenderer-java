package net.chroma.renderer;

/**
 * @author steinerb
 */
public interface Renderer {

    void renderNextImage(int imgWidth, int imgHeight);

    boolean isContinuous();

    byte[] get8BitRGBSnapshot();
}
