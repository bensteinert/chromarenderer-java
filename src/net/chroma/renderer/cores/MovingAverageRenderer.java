package net.chroma.renderer.cores;

import net.chroma.renderer.Renderer;
import utils.AccumulationBuffer;
import utils.ChromaCanvas;
import utils.MultiThreadedAccumulationBuffer;

/**
 * @author steinerb
 */
public class MovingAverageRenderer implements Renderer {

    private final AccumulationBuffer buffer;
    private final ChromaCanvas nextImage;
    private final RandomPixelGenerator generator = new RandomPixelGenerator(13499);


    public MovingAverageRenderer(int imgWidth, int imgHeight) {
        buffer = new MultiThreadedAccumulationBuffer(imgWidth, imgHeight);
        nextImage = new ChromaCanvas(imgWidth, imgHeight);
    }


    @Override
    public void renderNextImage(int imgWidth, int imgHeight, int widthOffset, int heightOffset) {
        generator.randomFloatPixels(nextImage.getPixels());
        buffer.accumulate(nextImage.getPixels());
    }


    @Override
    public boolean isContinuous() {
        return true;
    }


    @Override
    public byte[] get8BitRgbSnapshot() {
        return buffer.toByteImage();
    }

}
