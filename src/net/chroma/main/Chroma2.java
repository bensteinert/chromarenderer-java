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

    private boolean running = true;
    private boolean changed = false;
    private CountDownLatch renderLatch;
    private boolean shutDown;


    public Chroma2(int width, int height) {
        fpsCounter = new FpsCounter();
        imgWidth = width;
        imgHeight = height;
        renderer = new ColorCubeRenderer(imgWidth, imgHeight);
    }

    public float getFps() {
        return fpsCounter.getFps();
    }

    public byte[] getCurrentFrame() {
        changed = false;
        return renderer.get8BitRGBSnapshot();
    }

    @Override
    public void run() {
        while (running) {
            do {
                renderer.renderNextImage(imgWidth, imgHeight);
                changed = true;
                fpsCounter.frame();
            } while (renderer.isContinuous() && running);

            try {
                if (!shutDown) {
                    renderLatch = new CountDownLatch(1);
                    renderLatch.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        running = false;
        shutDown = true;
        if (renderLatch != null) {
            renderLatch.countDown();
        }
    }

    public void restart() {
        if (renderLatch != null) {
            renderLatch.countDown();
        }
    }

    public boolean hasChanges() {
        return changed;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }
}
