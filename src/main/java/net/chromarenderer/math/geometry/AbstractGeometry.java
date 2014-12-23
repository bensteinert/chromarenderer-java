package net.chromarenderer.math.geometry;

import net.chromarenderer.math.Vector3;

/**
 * @author steinerb
 */
public abstract class AbstractGeometry implements Geometry {

    private final Vector3 color;


    protected AbstractGeometry(Vector3 color) {
        this.color = color;
    }


    @Override
    public Vector3 getColor() {
        return color;
    }
}
