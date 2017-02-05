package net.chromarenderer;

/**
 * @author bensteinert
 */
public interface Camera {

    void move(float[] translation, float[] rotation);

    void resetToInitial();

    String getPositionAsString();

    String getCoordinateSystemAsString();
}
