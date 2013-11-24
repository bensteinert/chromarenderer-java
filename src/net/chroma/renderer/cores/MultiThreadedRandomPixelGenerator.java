//package net.chroma.renderer.cores;
//
//import net.chroma.Renderer;
//import net.chroma.math.Vector3;
//import net.chroma.math.random.MersenneTwisterFast;
//import utils.AccumulationBuffer;
//
//import java.util.concurrent.ForkJoinPool;
//import java.util.concurrent.RecursiveAction;
//
///**
// * @author steinerb
// */
//public class MultiThreadedRandomPixelGenerator extends RecursiveAction {
//
//    private final int threadId;
//    MersenneTwisterFast twister;
//
//
//    public MultiThreadedRandomPixelGenerator(long seed, int threadId) {
//        this.imgWidth = imgWidth;
//        this.imgHeight = imgHeight;
//        this.threadId = threadId;
//    }
//
//    protected Vector3[] randomFloatPixels(int imgWidth, int imgHeight) {
//        int count = imgWidth * imgHeight;
//        Vector3[] img = new Vector3[count];
//
//        for (int i = 0; i < count; i++) {
//            //pixels[i] = new Vector3(255.0f * (float)Math.random(), 255.0f * (float)Math.random(), 255.0f * (float)Math.random());
//            img[i] = new Vector3(255.0f * twister.nextFloat(), 255.0f * twister.nextFloat(), 255.0f * twister.nextFloat());
//        }
//
//        return img;
//    }
//
//
//    @Override
//    protected void compute() {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//}
