package net.chromarenderer.renderer.canvas;

import net.chromarenderer.math.Vector3;

/**
 * @author steinerb
 */
public class SingleThreadedAccumulationBuffer extends ChromaCanvas implements AccumulationBuffer {

    int accCount;


    public SingleThreadedAccumulationBuffer(int width, int height) {
        super(width, height);
        accCount = 0;
    }


    @Override
    public SingleThreadedAccumulationBuffer accumulate(Vector3[] input) {

        for (int i = 0; i < width * height; i++) {
            pixels[i] = pixels[i].mult(accCount).plus(input[i]).div(accCount + 1);
        }

        accCount++;
        return this;
    }


    @Override
    public void flushBuffer() {
        accCount = 0;
        flushCanvas();
    }

}
