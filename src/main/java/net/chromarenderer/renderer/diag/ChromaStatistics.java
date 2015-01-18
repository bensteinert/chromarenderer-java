package net.chromarenderer.renderer.diag;


import net.chromarenderer.utils.FpsCounter;

import java.util.concurrent.atomic.AtomicInteger;


public class ChromaStatistics {

    private final AtomicInteger reverseRaysMissed;
    private final AtomicInteger totalFrames;
    private final FpsCounter fpsCounter;


    public ChromaStatistics() {
        reverseRaysMissed = new AtomicInteger(0);
        totalFrames = new AtomicInteger(0);
        fpsCounter = new FpsCounter();
    }

    public void reverseRayMissed() {
        reverseRaysMissed.incrementAndGet();
    }

    public int getReverseRaysMissedCount(){
        return reverseRaysMissed.intValue();
    }

    public float getFps() {
        return fpsCounter.getFps();
    }


    public void reset() {
        reverseRaysMissed.set(0);
        totalFrames.set(0);
        fpsCounter.reset();
    }

    public void start() {
        fpsCounter.start();
    }

    public void frame() {
        totalFrames.incrementAndGet();
        fpsCounter.frame();
    }


    public Integer getTotalFrameCount() {
        return totalFrames.get();
    }
}
