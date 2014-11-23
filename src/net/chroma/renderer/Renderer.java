package net.chroma.renderer;

import net.chroma.renderer.diag.ChromaStatistics;

/**
 * @author steinerb
 */
public interface Renderer {

    void renderNextImage(int imgWidth, int imgHeight);

    boolean isContinuous();

    byte[] get8BitRgbSnapshot();

}
