package net.chromarenderer;

/**
 * @author bensteinert
 */
public interface Chroma extends Runnable {

    void start();

    void initialize(ChromaSettings settings);

    void takeScreenShot();

    void stop();

    void flushOnNextImage();

    boolean hasChanges();

    byte[] getCurrentFrame();

    ChromaSettings getSettings();

    Camera getCamera();
}
