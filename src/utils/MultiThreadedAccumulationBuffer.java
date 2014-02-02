package utils;

import net.chroma.math.Vector3;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * @author steinerb
 */
public class MultiThreadedAccumulationBuffer extends SingleThreadedAccumulationBuffer{

    private final ForkJoinPool pool;

    public MultiThreadedAccumulationBuffer(int width, int height) {
        super(width, height);
        pool = new ForkJoinPool();
    }

    @Override
    public MultiThreadedAccumulationBuffer accumulate(Vector3[] input) {
        if (input.length != width * height) {
            throw new IllegalArgumentException("Mismatching Accumulation buffer input!");
        }

        pool.invoke(new AccumulationTask(width, height, 0 , 0, input, true));
        pool.awaitQuiescence(1, TimeUnit.SECONDS);
        accCount++;
        return this;
    }


    private class AccumulationTask extends RecursiveAction{

        private final int taskWidth;
        private final int taskHeight;
        private final int offsetWidth;
        private final int offsetHeight;
        private final Vector3[] input;
        private final boolean divide;

        private AccumulationTask(int width, int height, int offsetWidth, int offsetHeight, Vector3[] input, boolean divide) {
            super();
            this.taskWidth = width;
            this.taskHeight = height;
            this.offsetWidth = offsetWidth;
            this.offsetHeight = offsetHeight;
            this.input = input;
            this.divide = divide;
        }

        @Override
        protected void compute() {
            // simple fork no core detection, multithreading could be meaningless here, above all (+1FPS vs Single-threaded) ;)
            if(!divide) {
                //System.out.println("Executing " + Thread.currentThread().getName());
                this.accumulate();
            } else {
                //System.out.println("Recursing " + Thread.currentThread().getName());
                int splitX = taskWidth / 2;
                int splitY = taskHeight / 2;
                AccumulationTask accumulationTask = new AccumulationTask(splitX, splitY, offsetWidth, offsetHeight, input, false);
                AccumulationTask accumulationTask1 = new AccumulationTask(splitX, splitY, offsetWidth + splitX, offsetHeight, input, false);
                AccumulationTask accumulationTask2 = new AccumulationTask(splitX, splitY, offsetWidth, offsetHeight + splitY, input, false);
                AccumulationTask accumulationTask3 = new AccumulationTask(splitX, splitY, offsetWidth + splitX, offsetHeight + splitY, input, false);
                invokeAll(
                        accumulationTask,
                        accumulationTask1,
                        accumulationTask2,
                        accumulationTask3
                );
            }
        }

        private void accumulate() {
            for(int y=0; y<taskHeight; y++) {
                int offsetX = (offsetHeight + y) * width + offsetWidth;

                for(int x = 0; x<taskWidth; x++){
                    int idx = offsetX + x;
                    pixels[idx] =  (pixels[idx].mult(accCount).plus(input[idx]).div(accCount+1));
                }
            }
        }
    }
}
