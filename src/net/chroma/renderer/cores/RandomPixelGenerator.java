package net.chroma.renderer.cores;

import net.chroma.math.Vector3;
import net.chroma.math.random.MersenneTwisterFast;

/**
 * @author steinerb
 */
public class RandomPixelGenerator {

    MersenneTwisterFast twister;

    public RandomPixelGenerator(long seed) {
        twister = new MersenneTwisterFast(seed);

    }

    protected byte[] random8BitPixels(int imgWidth, int imgHeight) {
        int count = imgWidth * imgHeight * 4; //RGBA
        byte[] pixels = new byte[count];

        for (int i = 0; i < count; i++) {
            pixels[i] = (byte) (Math.random() * 127);
        }

        return pixels;
    }

    protected void randomFloatPixels(Vector3[] pixels) {
        int count = pixels.length;

        for (int i = 0; i < count; i++) {
//            pixels[i] = new ImmutableVector3(
//                    255.0f * twister.nextFloat(),
//                    255.0f * twister.nextFloat(),
//                    255.0f * twister.nextFloat());
            pixels[i].set(255.0f * twister.nextFloat(), 255.0f * twister.nextFloat(), 255.0f * twister.nextFloat());
        }
    }
}
