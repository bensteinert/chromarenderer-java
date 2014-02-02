package net.chroma.renderer.cores;

import net.chroma.Renderer;

/**
 * @author steinerb
 */
public class WhittedRayTracer implements Renderer {

    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        return new byte[0];
    }

    @Override
    public boolean isContinuous() {
        return false;
    }
}
