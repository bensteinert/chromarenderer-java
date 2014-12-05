package net.chroma.renderer.core;

import net.chroma.renderer.Renderer;
import net.chroma.renderer.canvas.AccumulationBuffer;
import net.chroma.renderer.canvas.ChromaCanvas;
import net.chroma.renderer.canvas.MultiThreadedAccumulationBuffer;

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
