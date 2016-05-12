package net.chromarenderer.renderer.core;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.random.MersenneTwisterFast;

/**
 * @author steinerb
 */
public class ChromaThreadContext {

    //private static ThreadLocal<Integer> currentX = new ThreadLocal<>() ;
    //private static ThreadLocal<Integer> currentY = new ThreadLocal<>() ;
    private static ThreadLocal<MersenneTwisterFast> mt = ThreadLocal.withInitial(() ->
            new MersenneTwisterFast(Constants.getNextPrime())
    );


//    public static void setX(int x) {
//        currentX.set(x);
//    }
//
//    public static Integer getX() {
//        return currentX.get();
//    }
//
//    public static void setY(int y) {
//        currentY.set(y);
//    }
//
//    public static Integer getY() {
//        return currentY.get();
//    }


    public static float randomFloatClosedOpen() {
        return mt.get().nextFloat();
    }

    public static float randomFloatOpenOpen() {
        return mt.get().nextFloat(false, false);
    }

    public static double randomDoubleClosedOpen() {
        return mt.get().nextDouble();
    }

    public static double randomDoubleOpenOpen() {
        return mt.get().nextDouble(false, false);
    }

}
