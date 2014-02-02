package net.chroma.renderer.cores;

import net.chroma.Renderer;
import utils.AccumulationBuffer;
import utils.ChromaCanvas;
import utils.MultiThreadedAccumulationBuffer;

/**
 * @author steinerb
 */
public class MovingAverageRenderer implements Renderer {

    private AccumulationBuffer buffer;

    ChromaCanvas nextImage;

    private RandomPixelGenerator generator = new RandomPixelGenerator(13499);

    public MovingAverageRenderer(int imgWidth, int imgHeight) {
        buffer = new MultiThreadedAccumulationBuffer(imgWidth, imgHeight);
        //buffer = new SingleThreadedAccumulationBuffer(imgWidth, imgHeight);
        nextImage = new ChromaCanvas(imgWidth, imgHeight);
    }


    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        generator.randomFloatPixels(nextImage.getPixels());
        buffer.accumulate(nextImage.getPixels());
        return buffer.toByteImage();
    }

    @Override
    public boolean isContinuous() {
        return true;
    }

}
