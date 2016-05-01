package net.chromarenderer.math.geometry;

import net.chromarenderer.renderer.shader.Material;

/**
 * @author steinerb
 */
public abstract class AbstractGeometry implements Geometry {

    private final Material material;


    protected AbstractGeometry(Material material) {
        this.material = material;
    }


    @Override
    public Material getMaterial() {
        return material;
    }
}
