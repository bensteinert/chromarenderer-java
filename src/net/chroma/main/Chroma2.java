package net.chroma.main;

import net.chroma.renderer.ChromaRenderMode;
import net.chroma.renderer.Renderer;
import net.chroma.renderer.cores.ColorCubeRenderer;
import net.chroma.renderer.cores.MovingAverageRenderer;
import net.chroma.renderer.cores.SimpleRayTracer;
import net.chroma.renderer.diag.ChromaStatistics;
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


    private Renderer renderer;
    private int imgWidth;
    private int imgHeight;

    private final ChromaStatistics statistics;
    private boolean running = true;
    private boolean changed = false;
    private CountDownLatch renderLatch;
    private boolean shutDown;


    public Chroma2(int width, int height) {
        statistics = new ChromaStatistics();
        imgWidth = width;
        imgHeight = height;
        //renderer = new ColorCubeRenderer(imgWidth, imgHeight);
        renderer = new SimpleRayTracer(imgWidth, imgHeight, statistics);

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
                statistics.frame();
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
        statistics.reset();
        if (renderLatch != null) {
            renderLatch.countDown();
        }
    }

    public boolean hasChanges() {
        return changed;
    }

    private void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void init(ChromaRenderMode chromaRenderMode, int imgWidth, int imgHeight) {
        switch (chromaRenderMode) {
            case SIMPLE:
                setRenderer(new SimpleRayTracer(imgWidth, imgHeight, statistics));
                break;
            case AVG:
                setRenderer(new MovingAverageRenderer(imgWidth, imgHeight));
                break;
            case COLOR_CUBE:
                setRenderer(new ColorCubeRenderer(imgWidth, imgHeight));
                break;
            case WHITTED:
                break;
            default:
                break;
        }
    }

    public ChromaStatistics getStatistics() {
        return statistics;
    }
}
