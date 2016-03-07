package net.chromarenderer.main;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.geometry.PhotonFountain;
import net.chromarenderer.math.geometry.Sphere;
import net.chromarenderer.math.shader.Material;
import net.chromarenderer.math.shader.MaterialType;
import net.chromarenderer.math.shader.ShaderEngine;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.camera.PinholeCamera;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.core.ColorCubeRenderer;
import net.chromarenderer.renderer.core.MovingAverageRenderer;
import net.chromarenderer.renderer.core.SimplePathTracer;
import net.chromarenderer.renderer.core.SimpleRayTracer;
import net.chromarenderer.renderer.diag.ChromaStatistics;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.SceneFactory;
import net.chromarenderer.utils.TgaImageWriter;

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

    private final ChromaStatistics statistics;
    private boolean changed = false;
    private boolean restart = false;
    private CountDownLatch renderLatch;
    private ChromaSettings settings;
    private Camera camera;


    public Chroma() {
        statistics = new ChromaStatistics();
    }


    private List<Geometry> createSomeSpheres() {
        List<Geometry> result = new ArrayList<>();
        //result.add(new Sphere(new ImmutableVector3(0.0f, -0.3f, 0.0f), 0.1,   new Material(MaterialType.DIFFUSE, COLORS.BLUE)));
        //result.add(new Sphere(new ImmutableVector3(-1.0f, 1.0f, -1.0f), 0.2, new Material(MaterialType.DIFFUSE, COLORS.RED)));
        //result.add(new Sphere(new ImmutableVector3(1.0f, -0.4f, 1.0f), 0.2 , new Material(MaterialType.DIFFUSE, COLORS.PURPLE)));
        //result.add(new Sphere(new ImmutableVector3(-1.0f, 1.7f, -1.0f), 0.2, new Material(MaterialType.DIFFUSE, COLORS.GREEN)));
        //result.add(new Sphere(new ImmutableVector3(1.0f, -1.5f, -1.0f), 0.4, new Material(MaterialType.MIRROR, COLORS.WHITE)));
        //result.add(new Sphere(new ImmutableVector3(0.0f, 0.0f, 0.0f), 0.125, new Material(MaterialType.EMITTING, new ImmutableVector3(20, 20 ,200))));
        result.add(new PhotonFountain(new ImmutableVector3(0.f, 0.f, 0.f), 2000.f));
        return result;
    }


    public byte[] getCurrentFrame() {
        changed = false;
        return renderer.get8BitRgbSnapshot();
    }


    @Override
    public void run() {
        ChromaThreadContext.init();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                renderLatch = new CountDownLatch(1);
                renderLatch.await();
                restart = false;

                do {
                    renderer.renderNextImage(settings.getImgWidth(), settings.getImgWidth(), 0, 0);
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


    public void init(ChromaSettings settingsIn) {
        this.settings = new ChromaSettings(settingsIn);
        int pixelsX = this.settings.getImgWidth();
        int pixelsY = this.settings.getImgHeight();

        //RHS with depth along negative z-axis
        if (camera == null) {
            camera = new PinholeCamera(new ImmutableVector3(0.0f, 0.0f, 10.0f), 100.0f, 0.09f, 0.09f, pixelsX, pixelsY);
        }
        GeometryScene scene = SceneFactory.cornellBox(new ImmutableVector3(0, 0, 0), 2, createSomeSpheres());
        ShaderEngine.setScene(scene);

        switch (settings.getRenderMode()) {
            case SIMPLE:
                setRenderer(new SimpleRayTracer(settings, scene, camera, statistics));
                break;
            case AVG:
                setRenderer(new MovingAverageRenderer(settings));
                break;
            case COLOR_CUBE:
                setRenderer(new ColorCubeRenderer(settings));
                break;
            case PTDL:
                setRenderer(new SimplePathTracer(settings, scene, camera, statistics));
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


    public ChromaStatistics getStatistics() {
        return statistics;
    }


    public void takeScreenShot() {
        TgaImageWriter.writeTga(getCurrentFrame(), settings.getImgWidth(), settings.getImgHeight(), "/tmp/chroma/", "screenshot.tga");

    }


    public Camera getCamera() {
        return camera;
    }
}
