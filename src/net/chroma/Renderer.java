package net.chroma;

/**
 * @author steinerb
 */
public interface Renderer {

    byte[] renderNextImage(int imgWidth, int imgHeight);

    boolean isContinuous();
}
