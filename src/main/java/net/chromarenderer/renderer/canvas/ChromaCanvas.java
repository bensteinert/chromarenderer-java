package net.chromarenderer.renderer.canvas;

import net.chromarenderer.math.MutableVector3;

/**
 * @author steinerb
 */
public class ChromaCanvas {

    protected final MutableVector3[] pixels;
    protected final int width;
    protected final int height;


    public ChromaCanvas(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new MutableVector3[width * height];
        for (int i = 0; i < width * height; i++) {
            pixels[i] = new MutableVector3();
        }
        cleanCanvas();
    }


    public void cleanCanvas() {
        for (int i = 0; i < width * height; i++) {
            pixels[i].reset();
        }
    }


    public byte[] toByteImage() {
        int pixelCount = width * height;
        byte[] result = new byte[pixelCount * 3];
        for (int i = 0, j = 0; i < pixelCount; i += 1, j += 3) {
            result[j] = (byte) (255.0f * (pixels[i].getX()));
            result[j + 1] = (byte) (255.0f * (pixels[i].getY()));
            result[j + 2] = (byte) (255.0f * (pixels[i].getZ()));
        }

        return result;
    }


    public MutableVector3[] getPixels() {
        return pixels;
    }
}
