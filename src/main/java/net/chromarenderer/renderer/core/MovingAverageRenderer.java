package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.canvas.AccumulationBuffer;
import net.chromarenderer.renderer.canvas.ChromaCanvas;
import net.chromarenderer.renderer.canvas.MultiThreadedAccumulationBuffer;

/**
 * @author steinerb
 */
public class MovingAverageRenderer implements Renderer {

    private final AccumulationBuffer buffer;
    private final ChromaCanvas nextImage;
    private final RandomPixelGenerator generator = new RandomPixelGenerator(13499);


    public MovingAverageRenderer(ChromaSettings settings) {
        buffer = new MultiThreadedAccumulationBuffer(settings.getImgWidth(), settings.getImgHeight());
        nextImage = new ChromaCanvas(settings.getImgWidth(), settings.getImgHeight());
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
    public void flush() {

    }


    @Override
    public byte[] get8BitRgbSnapshot() {
        return buffer.to8BitImage();
    }

}
