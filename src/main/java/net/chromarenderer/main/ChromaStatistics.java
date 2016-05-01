package net.chromarenderer.main;


import net.chromarenderer.utils.FpsCounter;

import java.util.concurrent.atomic.AtomicLong;


public class ChromaStatistics {

    private static final AtomicLong subsurfaceHitpointCorrected = new AtomicLong(0);
    private static final AtomicLong totalFrames = new AtomicLong(0);

    private static final AtomicLong rayCounter = new AtomicLong(0);
    private static final AtomicLong intersectionCounter = new AtomicLong(0);

    private static final FpsCounter fpsCounter = new FpsCounter();


    public static void subsurfaceHitpointCorrected() {
        subsurfaceHitpointCorrected.incrementAndGet();
    }


    public static int getSubsurfaceCorrectionsCount() {
        return subsurfaceHitpointCorrected.intValue();
    }


    public static float getFps() {
        return fpsCounter.getFps();
    }


    static void reset() {
        subsurfaceHitpointCorrected.set(0);
        totalFrames.set(0);
        fpsCounter.reset();
        intersectionCounter.set(0);
        rayCounter.set(0);
    }


    static void frame() {
        totalFrames.incrementAndGet();
        fpsCounter.frame();
    }


    public static void ray() {
        rayCounter.incrementAndGet();
    }


    public static long getRayCountAndFlush() {
        return rayCounter.getAndSet(0);
    }


    public static long getTotalFrameCount() {
        return totalFrames.get();
    }


    public static long getIntersectionsCountAndFlush() {
        return intersectionCounter.getAndSet(0);
    }


    public static void intersectOp() {
        intersectionCounter.getAndIncrement();
    }
}
