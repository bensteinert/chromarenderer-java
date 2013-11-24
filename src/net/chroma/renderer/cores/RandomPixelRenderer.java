package net.chroma.renderer.cores;

import net.chroma.Renderer;
import net.chroma.math.Vector3;
import utils.AccumulationBuffer;
import utils.ForkAccumulationBuffer;
import utils.SingleThreadedAccumulationBuffer;

/**
 * @author steinerb
 */
public class RandomPixelRenderer implements Renderer {

    private AccumulationBuffer buffer;

    private RandomPixelGenerator generator = new RandomPixelGenerator(13499);

    public RandomPixelRenderer(int imgWidth, int imgHeight) {
        //buffer = new ForkAccumulationBuffer(imgWidth, imgHeight);
        buffer = new SingleThreadedAccumulationBuffer(imgWidth, imgHeight);
    }


    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        Vector3[] vector3s = generator.randomFloatPixels(imgWidth, imgHeight);
        buffer.accumulate(vector3s);
        return buffer.toByteImage();
    }
}
