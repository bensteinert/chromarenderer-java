package net.chromarenderer.main;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.camera.PinholeCamera;
import net.chromarenderer.renderer.core.ColorCubeRenderer;
import net.chromarenderer.renderer.core.MonteCarloPathTracer;
import net.chromarenderer.renderer.core.MovingAverageRenderer;
import net.chromarenderer.renderer.core.SimpleRayCaster;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.FurnaceTest;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.SceneFactory;
import net.chromarenderer.renderer.shader.ShaderEngine;
import net.chromarenderer.utils.BlenderChromaImporter;
import net.chromarenderer.utils.TgaImageWriter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.LogManager;

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


    static {
        try {
            LogManager.getLogManager().readConfiguration(Chroma.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Renderer renderer;
    private boolean changed = false;
    private boolean breakLoop = false;
    private CountDownLatch renderLatch;
    private ChromaSettings settings;

    private Camera camera;
    private ChromaScene scene;
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

                if (!sceneAndSettingsOk()) {
                    ChromaLogger.get().severe("Scene cannot be rendered with the current settings, Please check earlier errors and warnings in the logs.");
                    continue;
                }

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


    private boolean sceneAndSettingsOk() {
        // currently there is no environmental light model ...

        ChromaLogger.get().log(Level.INFO, "Number of light sources in the scene: {0}", scene.getNumberOfLightSources());

        final boolean lightSourcesOk = scene.getNumberOfLightSources() > 0;

        return lightSourcesOk;
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


    public void initialize(ChromaSettings settingsIn) {

        boolean initScene = true;
        boolean initAccStruct = true;

        // Save some time when working with large scenes ...
        if (settings != null) {
            if (Objects.equals(settings.getSceneType(), settingsIn.getSceneType())) {
                initScene = false;
            }
            if (settings.getAccStructType().equals(settingsIn.getAccStructType())) {
                initAccStruct = false;
            }
        }

        this.settings = settingsIn;

        if (initScene) {
            switch (settings.getSceneType()) {
                case BLENDER_EXPORT:
                    GeometryScene geometryScene = null;
                    try {
                        geometryScene = BlenderChromaImporter.importSceneFromFile(settings.getScenePath(), settings.getSceneName() + ".blend");
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    scene = geometryScene;
                    camera = geometryScene.getCamera();
                    break;
                case FURNACE_TEST:
                    scene = new FurnaceTest();
                    camera = new PinholeCamera(new ImmutableVector3(1, 1, 5), settings.getImgWidth(), settings.getImgHeight());
                    break;
                case CORNELL_BOX:
                    scene = SceneFactory.cornellBox(new ImmutableVector3(0, 0, 0), 2, SceneFactory.createSomeSpheres());
                    camera = new PinholeCamera(new ImmutableVector3(0, -0.7, 5.4), settings.getImgWidth(), settings.getImgHeight());
                    break;
            }
        }

        camera.recalibrateSensor(settings.getImgWidth(), settings.getImgHeight());

        if (scene instanceof GeometryScene && (initAccStruct || initScene)) {
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

        ChromaStatistics.reset();
    }


    public void takeScreenShot() {
        TgaImageWriter.writeTga(getCurrentFrame(), settings.getImgWidth(), settings.getImgHeight(), System.getProperty("user.dir") + "/chroma/screenshots/" + settings.getSceneName() + "/", settings.getSceneName() + "-" + System.currentTimeMillis() + ".tga");
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
