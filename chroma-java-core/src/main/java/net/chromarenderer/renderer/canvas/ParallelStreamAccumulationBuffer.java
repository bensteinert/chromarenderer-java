package net.chromarenderer.renderer.canvas;

import net.chromarenderer.math.Vector3;

import java.util.stream.IntStream;

/**
 * @author bensteinert
 */
public class ParallelStreamAccumulationBuffer extends SingleThreadedAccumulationBuffer {


    public ParallelStreamAccumulationBuffer(int width, int height) {
        super(width, height);
    }


    @Override
    public ParallelStreamAccumulationBuffer accumulate(Vector3[] input) {
        // code duplication from SingleThreadedAccumulationBuffer on purpose.
        // 10% performance loss when DRYED up in common method...
        final int next = accCount + 1;
        IntStream.range(0, input.length).parallel().forEach(i ->
                pixels[i].mult(accCount).plus(input[i]).div(next)
        );

        accCount++;
        return this;
    }
}
