package net.chroma.math.geometry;

import net.chroma.math.Vector3;

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
