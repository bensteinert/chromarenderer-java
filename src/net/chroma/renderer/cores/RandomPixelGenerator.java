package net.chroma.renderer.cores;

import net.chroma.Renderer;

/**
 * @author steinerb
 */
public class RandomPixelGenerator implements Renderer {

    private byte[] randomPixels(int imgWidth, int imgHeight) {
        int count = imgWidth * imgHeight * 4; //RGBA
        byte[] pixels = new byte[count];

        for (int i = 0; i < count; i++) {
            pixels[i] = (byte) (Math.random() * 255);
            //pixels[i] = i% 1023 == 0 ? (byte) 128 : 0;
        }

        return pixels;
    }

    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        return randomPixels(imgWidth, imgHeight);
    }
}
