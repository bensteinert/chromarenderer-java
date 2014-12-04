package net.chroma.renderer.diag;

import utils.FpsCounter;

import java.util.concurrent.atomic.AtomicInteger;


public class ChromaStatistics {

    private final AtomicInteger reverseRaysMissed;
    private final FpsCounter fpsCounter;


    public ChromaStatistics() {
        reverseRaysMissed = new AtomicInteger(0);
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
    }

    public void frame() {
        fpsCounter.frame();
    }
}
