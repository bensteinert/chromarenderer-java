package net.chromarenderer.main;


import net.chromarenderer.utils.FpsCounter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class ChromaStatistics {

    private static final AtomicInteger reverseRaysMissed = new AtomicInteger(0);
    private static final AtomicInteger totalFrames = new AtomicInteger(0);
    private static final AtomicInteger rayCount = new AtomicInteger(0);
    private static final AtomicLong intersectionCounter = new AtomicLong(0);
    private static final FpsCounter fpsCounter = new FpsCounter();


    public static void reverseRayMissed() {
        reverseRaysMissed.incrementAndGet();
    }


    public static int getReverseRaysMissedCount() {
        return reverseRaysMissed.intValue();
    }


    public static float getFps() {
        return fpsCounter.getFps();
    }


    public static void reset() {
        reverseRaysMissed.set(0);
        totalFrames.set(0);
        fpsCounter.reset();
        intersectionCounter.set(0);
    }


    public static void start() {
        fpsCounter.start();
    }


    public static void frame() {
        totalFrames.incrementAndGet();
        fpsCounter.frame();
    }


    public static void ray() {
        rayCount.incrementAndGet();
    }


    public static int getRayCountAndFlush() {
        return rayCount.getAndSet(0);
    }


    public static Integer getTotalFrameCount() {
        return totalFrames.get();
    }


    public static Long getTotalIntersectionsAndFlush() {
        return intersectionCounter.getAndSet(0);
    }


    public static void intersectOp() {
        intersectionCounter.getAndIncrement();

    }
}
