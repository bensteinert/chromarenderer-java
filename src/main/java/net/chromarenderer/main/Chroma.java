package net.chromarenderer.main;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.geometry.Sphere;
import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.camera.PinholeCamera;
import net.chromarenderer.renderer.core.ColorCubeRenderer;
import net.chromarenderer.renderer.core.DistributionRayTracer;
import net.chromarenderer.renderer.core.MovingAverageRenderer;
import net.chromarenderer.renderer.core.SimpleRayTracer;
import net.chromarenderer.renderer.diag.ChromaStatistics;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.SceneFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author steinerb
 */
public class Chroma implements Runnable {

//    public static Unsafe UNSAFE;
//
//
//    static {
//        try {
//            Field f;
//            f = Unsafe.class.getDeclaredField("theUnsafe");
//            f.setAccessible(true);
//            UNSAFE = (Unsafe) f.get(null);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }


    private Renderer renderer;
    private final int imgWidth;
    private final int imgHeight;

    private final Camera camera;
    private final GeometryScene scene;
    private final ChromaStatistics statistics;
    private boolean changed = false;
    private boolean restart = false;
    private CountDownLatch renderLatch;


    public Chroma(int width, int height) {
        statistics = new ChromaStatistics();
        imgWidth = width;
        imgHeight = height;
        scene = SceneFactory.cornellBox(new ImmutableVector3(0, 0, 0), 2, createSomeSpheres());
        //RHS with depth along negative z-axis
        camera = new PinholeCamera(new ImmutableVector3(0.0f, 0.0f, 6.0f), 0.1f, 0.00009f, 0.00009f, width, height);
        renderer = new SimpleRayTracer(imgWidth, imgHeight, scene, camera, statistics);
    }

    private List<Geometry> createSomeSpheres() {
        List<Geometry> result = new ArrayList<>();
        result.add(new Sphere(new ImmutableVector3(0.0f, 0.0f, 0.0f), 0.2, COLORS.PURPLE));
        result.add(new Sphere(new ImmutableVector3(-1.0f, 1.0f, -1.0f), 0.2 , COLORS.RED));
        result.add(new Sphere(new ImmutableVector3(1.0f, -1.0f, 1.0f), 0.2 , COLORS.BLUE));
        result.add(new Sphere(new ImmutableVector3(-1.0f, 1.7f, -1.0f), 0.2, COLORS.GREY));
        result.add(new Sphere(new ImmutableVector3(1.0f, -1.7f, -1.0f), 0.2, COLORS.GREEN));
        return result;
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
                setRenderer(new SimpleRayTracer(imgWidth, imgHeight, scene, camera, statistics));
                break;
            case AVG:
                setRenderer(new MovingAverageRenderer(imgWidth, imgHeight));
                break;
            case COLOR_CUBE:
                setRenderer(new ColorCubeRenderer(imgWidth, imgHeight));
                break;
            case DISTRIBUTION:
                setRenderer(new DistributionRayTracer(imgWidth, imgHeight, scene, camera));
                break;
            default:
                break;
        }
    }


    public ChromaStatistics getStatistics() {
        return statistics;
    }
}
