package net.chroma.renderer;

/**
 * @author steinerb
 */
public interface Renderer {

    byte[] renderNextImage(int imgWidth, int imgHeight);

    boolean isContinuous();
}
