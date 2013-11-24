package utils;

import net.chroma.math.Vector3;

/**
 * @author steinerb
 */
public class SingleThreadedAccumulationBuffer implements AccumulationBuffer {

    protected int width;
    protected int height;
    protected Vector3[] pixels;
    protected int accCount;

    public SingleThreadedAccumulationBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Vector3[width * height];
        for (int i = 0; i < width * height; i++) {
            pixels[i] = new Vector3(.0f, 0.f, .0f);
        }
        accCount = 0;
    }

    @Override
    public SingleThreadedAccumulationBuffer accumulate(Vector3[] input) {
        if (input.length != width * height) {
            throw new IllegalArgumentException("Mismatching Accumulation buffer input!");
        }
        // maybe using collection streams?

        for (int i = 0; i < width * height; i++) {
            pixels[i] =  pixels[i].mult(accCount).plus(input[i]).div(accCount+1);
        }

        accCount++;

        return this;
    }

    @Override
    public byte[] toByteImage() {

        int pixelCount = width * height;
        byte[] result = new byte[pixelCount * 4];
        for (int i = 0; i < pixelCount; i+=4) {
            result[i] = (byte) (pixels[i].getZ());
            result[i + 1] = (byte) (pixels[i].getY());
            result[i + 2] = (byte) (pixels[i].getX());
            result[i + 3] = (byte) -1;

        }

        return result;
    }


}
