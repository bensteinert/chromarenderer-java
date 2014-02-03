package net.chroma.main;

import net.chroma.Renderer;
import net.chroma.renderer.cores.ColorCubeRenderer;
import utils.FpsCounter;

import java.util.concurrent.CountDownLatch;

/**
 * @author steinerb
 */
public class Chroma2 implements Runnable{

    private FpsCounter fpsCounter;
    private Renderer renderer;
    private int imgWidth;
    private int imgHeight;
    private byte[] currentFrame;

    private boolean running = false;
    private boolean changed = false;
    private CountDownLatch countDownLatch;


    public Chroma2(int width, int height) {
        fpsCounter = new FpsCounter();
        imgWidth = width;
        imgHeight = height;
        currentFrame = new byte[imgHeight * imgHeight * 3];
        renderer = new ColorCubeRenderer(imgWidth, imgHeight);
    }

    public float getFps(){
        return fpsCounter.getFps();
    }

    public byte[] getCurrentFrame() {
        changed = false;
        return currentFrame;
    }

    @Override
    public void run() {
        running = true;
        while(running) {
            do {
                currentFrame = renderer.renderNextImage(imgWidth, imgHeight);
                changed = true;
                fpsCounter.frame();
            } while(renderer.isContinuous());

            try {
                countDownLatch = new CountDownLatch(1);
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish() {
        running = false;
        if(countDownLatch != null){
            countDownLatch.countDown();
        }
    }

    public void restart(){
        if(countDownLatch != null){
            countDownLatch.countDown();
        }
    }

    public boolean hasChanges(){
        return changed;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
}
