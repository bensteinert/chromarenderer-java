package net.chroma.renderer;

/**
 * @author steinerb
 */
public interface Renderer {

    /**
     * Renders the currently set scene with the defined camera. Params allow sub image selection.
     *
     * @param imgWidth     x dimension of the camera resolution to take into account. Should not be > than witdhX of the according camera.
     * @param imgHeight    y dimension of the camera resolution to take into account. Should not be > than witdhY of the according camera.
     * @param widthOffset  x offset where to start sub-select of image part to render.
     * @param heightOffset y offset where to start sub-select of image part to render.
     */
    void renderNextImage(int imgWidth, int imgHeight, int widthOffset, int heightOffset);

    boolean isContinuous();

    byte[] get8BitRgbSnapshot();

}
