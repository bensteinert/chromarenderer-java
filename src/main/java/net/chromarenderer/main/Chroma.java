package net.chromarenderer.main;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.camera.PinholeCamera;
import net.chromarenderer.renderer.core.ColorCubeRenderer;
import net.chromarenderer.renderer.core.MonteCarloPathTracer;
import net.chromarenderer.renderer.core.MovingAverageRenderer;
import net.chromarenderer.renderer.core.SimpleRayCaster;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.shader.ShaderEngine;
import net.chromarenderer.utils.TgaImageWriter;

import java.util.concurrent.CountDownLatch;

/**
 * @author bensteinert
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
    private boolean changed = false;
    private boolean breakLoop = false;
    private CountDownLatch renderLatch;
    private ChromaSettings settings;
    private Camera camera;
    private boolean needsFlush;


    public Chroma() {
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
                breakLoop = false;

                do {
                    if (needsFlush) {
                        flushRenderer();
                        ChromaStatistics.reset();
                        needsFlush = false;
                    }
                    renderer.renderNextImage();
                    if (settings.computeL1Norm()) {
                        ChromaStatistics.L1Norm = renderer.computeL1Norm();
                    }
                    changed = true;
                    ChromaStatistics.frame();
                } while (!Thread.currentThread().isInterrupted() && !breakLoop);


            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    public void start() {
        if (renderLatch != null) {
            renderLatch.countDown();
        }
    }

    public void stop() {
        breakLoop = true;
    }


    public boolean hasChanges() {
        return changed;
    }


    private void setRenderer(Renderer renderer) {
        breakLoop = true;
        this.renderer = renderer;
    }


    public void reinit(ChromaSettings settingsIn, ChromaScene scene) {
        this.settings = settingsIn;
        int pixelsX = this.settings.getImgWidth();
        int pixelsY = this.settings.getImgHeight();

        //RHS with depth along negative z-axis
        if (camera == null) {
            camera = new PinholeCamera(new ImmutableVector3(0.0f, 0.0f, 10.0f), 100.0f, 0.09f, 0.09f, pixelsX, pixelsY);
        } else {
            camera.recalibrateSensor(settings.getImgWidth(), settings.getImgHeight());
        }

        if(scene instanceof GeometryScene) {
            ((GeometryScene) scene).buildAccelerationStructure(settings.getAccStructType());
        }

        ShaderEngine.setScene(scene);

        switch (settings.getRenderMode()) {
            case SIMPLE:
                setRenderer(new SimpleRayCaster(settings, scene, camera));
                break;
            case AVG:
                setRenderer(new MovingAverageRenderer(settings));
                break;
            case COLOR_CUBE:
                setRenderer(new ColorCubeRenderer(settings));
                break;
            case MT_PTDL:
                setRenderer(new MonteCarloPathTracer(settings, scene, camera));
                break;
            default:
                break;
        }
    }

    public void moveCamera(Vector3 translation, Vector3 rotation){
        if (translation.nonZero() || rotation.nonZero()) {
            camera.move(translation, rotation);
        }
    }


    public void takeScreenShot() {
        TgaImageWriter.writeTga(getCurrentFrame(), settings.getImgWidth(), settings.getImgHeight(), "./tmp/chroma/", "screenshot" + System.currentTimeMillis() + ".tga");

    }


    public Camera getCamera() {
        return camera;
    }


    public ChromaSettings getSettings() {
        return settings;
    }


    private void flushRenderer() {
        renderer.flush();
    }


    public void flushOnNextImage() {
        needsFlush = true;
    }
}
