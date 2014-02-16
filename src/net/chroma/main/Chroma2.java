package net.chroma.main;

import net.chroma.renderer.Renderer;
import net.chroma.renderer.cores.ColorCubeRenderer;
import sun.misc.Unsafe;
import utils.FpsCounter;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

/**
 * @author steinerb
 */
public class Chroma2 implements Runnable {

    public static Unsafe UNSAFE;

    static {
        try {
            Field f;
            f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private FpsCounter fpsCounter;
    private Renderer renderer;
    private int imgWidth;
    private int imgHeight;
    private byte[] currentFrame;

    private boolean running = true;
    private boolean changed = false;
    private CountDownLatch countDownLatch;
    private boolean shutDown;


    public Chroma2(int width, int height) {
        fpsCounter = new FpsCounter();
        imgWidth = width;
        imgHeight = height;
        currentFrame = new byte[imgHeight * imgHeight * 3];
        renderer = new ColorCubeRenderer(imgWidth, imgHeight);
    }

    public float getFps() {
        return fpsCounter.getFps();
    }

    public byte[] getCurrentFrame() {
        changed = false;
        return currentFrame;
    }

    @Override
    public void run() {
        while (running) {
            do {
                currentFrame = renderer.renderNextImage(imgWidth, imgHeight);
                changed = true;
                fpsCounter.frame();
            } while (renderer.isContinuous());

            try {
                if (!shutDown) {
                    countDownLatch = new CountDownLatch(1);
                    countDownLatch.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        running = false;
        shutDown = true;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void restart() {
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public boolean hasChanges() {
        return changed;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
}
