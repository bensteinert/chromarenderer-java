package net.chromarenderer.renderer;

/**
 * @author bensteinert
 */
public interface Renderer {

    void renderNextImage();

    void flush();

    byte[] get8BitRgbSnapshot();

    float computeL1Norm();
}
