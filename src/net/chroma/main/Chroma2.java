package net.chroma.main;

import net.chroma.Renderer;
import net.chroma.renderer.cores.MovingAverageRenderer;
import utils.FpsCounter;

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


    public Chroma2(int width, int height) {
        fpsCounter = new FpsCounter();
        imgWidth = width;
        imgHeight = height;
        currentFrame = new byte[imgHeight * imgHeight * 3];
        renderer = new MovingAverageRenderer(imgWidth, imgHeight);
        //renderer = new ColorCubeRenderer(imgWidth, imgHeight);
        //renderer = new SimpleRayTracer(imgWidth, imgHeight);
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
         do {
             currentFrame = renderer.renderNextImage(imgWidth, imgHeight);
             changed = true;
             fpsCounter.frame();
         } while(running && renderer.isContinuous());
    }

    public void finish() {
        running = false;
    }

    public boolean hasChanges(){
        return changed;
    }

}
