package net.chroma.main;

import net.chroma.math.ImmutableVector3;
import net.chroma.renderer.ChromaRenderMode;
import net.chroma.renderer.Renderer;
import net.chroma.renderer.cameras.Camera;
import net.chroma.renderer.cameras.PinholeCamera;
import net.chroma.renderer.cores.ColorCubeRenderer;
import net.chroma.renderer.cores.MovingAverageRenderer;
import net.chroma.renderer.cores.SimpleRayTracer;
import net.chroma.renderer.diag.ChromaStatistics;
import sun.misc.Unsafe;

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
    private final int imgWidth;
    private final int imgHeight;

    private final Camera camera;
    private final ChromaStatistics statistics;
    private boolean changed = false;
    private boolean restart = false;
    private CountDownLatch renderLatch;


    public Chroma2(int width, int height) {
        statistics = new ChromaStatistics();
        imgWidth = width;
        imgHeight = height;
        camera = new PinholeCamera(new ImmutableVector3(0.0f, 0.0f, 8.0f), 0.1f, 0.0001f, 0.0001f, width, height);
        renderer = new SimpleRayTracer(imgWidth, imgHeight, camera, statistics);
    }


    public byte[] getCurrentFrame() {
        changed = false;
        return renderer.get8BitRgbSnapshot();
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                renderLatch = new CountDownLatch(1);
                renderLatch.await();
                restart = false;

                do {
                    renderer.renderNextImage(imgWidth, imgHeight, 0, 0);
                    changed = true;
                    statistics.frame();
                } while (renderer.isContinuous() && !Thread.currentThread().isInterrupted() && !restart);


            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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
        restart = true;
        this.renderer = renderer;
    }


    public void init(ChromaRenderMode chromaRenderMode, int imgWidth, int imgHeight) {
        switch (chromaRenderMode) {
            case SIMPLE:
                setRenderer(new SimpleRayTracer(imgWidth, imgHeight, camera, statistics));
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
