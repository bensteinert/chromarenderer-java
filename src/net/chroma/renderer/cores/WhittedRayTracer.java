package net.chroma.renderer.cores;

import net.chroma.renderer.Renderer;

/**
 * @author steinerb
 */
public class WhittedRayTracer implements Renderer {

    @Override
    public void renderNextImage(int imgWidth, int imgHeight, int widthOffset, int heightOffset) {

    }

    @Override
    public boolean isContinuous() {
        return false;
    }

    @Override
    public byte[] get8BitRgbSnapshot() {
        return new byte[0];
    }
}
