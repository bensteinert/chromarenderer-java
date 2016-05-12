package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.canvas.AccumulationBuffer;
import net.chromarenderer.renderer.canvas.ChromaCanvas;
import net.chromarenderer.renderer.canvas.ParallelStreamAccumulationBuffer;
import net.chromarenderer.renderer.scene.ChromaScene;

import java.util.stream.IntStream;

/**
 * @author bensteinert
 */
abstract class AccumulativeRenderer extends ChromaCanvas implements Renderer {

    private final AccumulationBuffer buffer;
    final ChromaSettings settings;
    final Camera camera;
    final ChromaScene scene;


    AccumulativeRenderer(ChromaSettings settings, ChromaScene scene, Camera camera) {
        super(settings.getImgWidth(), settings.getImgHeight());
        this.settings = settings;
        buffer = new ParallelStreamAccumulationBuffer(settings.getImgWidth(), settings.getImgHeight());
        this.scene = scene;
        this.camera = camera;
    }

    @Override
    public void renderNextImage() {
        if (settings.isMultiThreaded()) {
            IntStream.range(0, settings.getImgHeight()).parallel().forEach(j ->
                    IntStream.range(0, settings.getImgWidth()).parallel().forEach(i -> {
                        renderPixel(j, i);
                    })
            );
        }
        else {
            for (int j = 0; j < settings.getImgHeight(); j += 1) {
                for (int i = 0; i < settings.getImgWidth(); i += 1) {
                    renderPixel(j, i);
                }
            }
        }

        buffer.accumulate(getPixels());
    }


    protected abstract void renderPixel(int j, int i);


    @Override
    public void flush() {
        flushCanvas();
        buffer.flushBuffer();
    }


    @Override
    public byte[] get8BitRgbSnapshot() {
        return buffer.to8BitImage();
    }


}
