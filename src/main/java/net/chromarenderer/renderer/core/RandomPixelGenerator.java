package net.chromarenderer.renderer.core;

import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.random.MersenneTwisterFast;
import org.apache.commons.math3.util.FastMath;

/**
 * @author bensteinert
 */
public class RandomPixelGenerator {

    private final MersenneTwisterFast twister;


    public RandomPixelGenerator(long seed) {
        twister = new MersenneTwisterFast(seed);
    }


    protected byte[] random8BitPixels(int imgWidth, int imgHeight) {
        int count = imgWidth * imgHeight * 4; //RGBA
        byte[] pixels = new byte[count];

        for (int i = 0; i < count; i++) {
            pixels[i] = (byte) (FastMath.random() * 127);
        }

        return pixels;
    }

    protected void randomFloatPixels(MutableVector3[] pixels) {
        int count = pixels.length;

        for (int i = 0; i < count; i++) {
            pixels[i].set(twister.nextFloat(), twister.nextFloat(),twister.nextFloat());
        }
    }
}
