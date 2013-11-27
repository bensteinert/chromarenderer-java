package utils;

import net.chroma.math.Vector3;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @author steinerb
 */
public class ForkAccumulationBuffer extends SingleThreadedAccumulationBuffer{

    private final int cores;

    private final ForkJoinPool pool;

    public ForkAccumulationBuffer(int width, int height) {
        super(width, height);
        cores = Runtime.getRuntime().availableProcessors();
        pool = new ForkJoinPool();
    }

    @Override
    public ForkAccumulationBuffer accumulate(Vector3[] input) {
        if (input.length != width * height) {
            throw new IllegalArgumentException("Mismatching Accumulation buffer input!");
        }

        pool.invoke(new AccumulationTask(width, height, 0 , 0, input, 0));

        accCount++;

        return this;
    }


    private class AccumulationTask extends RecursiveAction{

        private final int taskWidth;
        private final int taskHeight;
        private final int offsetWidth;
        private final int offsetHeight;
        private final Vector3[] input;
        private final int depth;

        private AccumulationTask(int width, int height, int offsetWidth, int offsetHeight, Vector3[] input, int depth) {
            super();
            this.taskWidth = width;
            this.taskHeight = height;
            this.offsetWidth = offsetWidth;
            this.offsetHeight = offsetHeight;
            this.input = input;
            this.depth = depth;
        }

        @Override
        protected void compute() {
            if(cores <= depth * 4) {
                accumulate();
            } else {
                int splitX = width / 2;
                int splitY = height / 2;
                invokeAll(
                    new AccumulationTask(splitX, splitY, offsetWidth, offsetHeight, input, 1),
                    new AccumulationTask(splitX, splitY, offsetWidth + splitX, offsetHeight, input, 2),
                    new AccumulationTask(splitX, splitY, offsetWidth, offsetHeight + splitY, input, 3)
                    //new AccumulationTask(splitX, splitY, offsetWidth + splitX, offsetHeight + splitY, input, 4)
                );

            }

        }

        private void accumulate() {
            Vector3 project;
            switch (depth){
                case 1:
                    project = new Vector3(1.0f, .0f, .0f);
                    break;
                case 2:
                    project = new Vector3(.0f, 1.0f, .0f);
                    break;
                case 3:
                    project = new Vector3(.0f, .0f, 1.0f);
                    break;
                default:
                    project = new Vector3(.0f, .0f, 0.0f);
                    break;
            }

            for(int y = 0; y<taskHeight; y++) {
                int offsetX = (offsetHeight + y) * (width-1) + offsetWidth;

                for(int x = 0; x<taskWidth; x++){
                    int idx = offsetX + x;
                    //pixels[idx] = project.mult(255.0f);
                    pixels[idx] =  (pixels[idx].mult(accCount).plus(input[idx]).div(accCount+1)).mult(project);
                }
            }
        }
    }
}
