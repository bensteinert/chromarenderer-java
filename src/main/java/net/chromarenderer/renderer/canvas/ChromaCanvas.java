package net.chromarenderer.renderer.canvas;

import net.chromarenderer.math.MutableVector3;
import org.apache.commons.math3.util.FastMath;

import java.util.stream.IntStream;

/**
 * @author bensteinert
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
        flushCanvas();
    }


    public void flushCanvas() {
        for (int i = 0; i < width * height; i++) {
            pixels[i].reset();
        }
    }


    public byte[] to8BitImage() {
        int pixelCount = width * height;
        byte[] result = new byte[pixelCount * 3];
        IntStream.range(0, pixelCount).parallel().forEach(i -> {
            int j = i * 3;
            result[j] =     (byte) (255.0f * (FastMath.min(pixels[i].getX(), 1.0f)));
            result[j + 1] = (byte) (255.0f * (FastMath.min(pixels[i].getY(), 1.0f)));
            result[j + 2] = (byte) (255.0f * (FastMath.min(pixels[i].getZ(), 1.0f)));
        });
        return result;
    }


    public MutableVector3[] getPixels() {
        return pixels;
    }
}
