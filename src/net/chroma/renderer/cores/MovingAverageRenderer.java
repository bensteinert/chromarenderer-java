package net.chroma.renderer.cores;

import net.chroma.Renderer;
import net.chroma.math.COLORS;
import net.chroma.math.Vector3;
import utils.AccumulationBuffer;
import utils.ChromaCanvas;
import utils.ExecutionTimer;
import utils.MultiThreadedAccumulationBuffer;
import utils.SingleThreadedAccumulationBuffer;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author steinerb
 */
public class MovingAverageRenderer implements Renderer {

    private AccumulationBuffer buffer;

    ChromaCanvas input;

    private RandomPixelGenerator generator = new RandomPixelGenerator(13499);

    public MovingAverageRenderer(int imgWidth, int imgHeight) {
        //buffer = new MultiThreadedAccumulationBuffer(imgWidth, imgHeight);
        buffer = new SingleThreadedAccumulationBuffer(imgWidth, imgHeight);
        input = new ChromaCanvas(imgWidth, imgHeight);
    }


    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        generator.randomFloatPixels(input.getPixels());
        //ColorCubeRenderer.createCubes(input.getPixels(), imgWidth, imgHeight);
        buffer.accumulate(input.getPixels());
        return buffer.toByteImage();
    }

}
