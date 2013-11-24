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

    protected Vector3[] randomFloatPixels(int imgWidth, int imgHeight) {
        int count = imgWidth * imgHeight;
        Vector3[] img = new Vector3[count];

        for (int i = 0; i < count; i++) {
            //pixels[i] = new Vector3(255.0f * (float)Math.random(), 255.0f * (float)Math.random(), 255.0f * (float)Math.random());
            //img[i] = new Vector3(255.0f * twister.nextFloat(), 255.0f * twister.nextFloat(), 255.0f * twister.nextFloat());
            img[i] = new Vector3(255.0f,.0f,.0f);
        }

        return img;
    }
}
