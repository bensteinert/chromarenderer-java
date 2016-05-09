package net.chromarenderer.math.geometry;

import net.chromarenderer.renderer.shader.Material;

/**
 * @author bensteinert
 */
abstract class AbstractGeometry implements Geometry {

    private Material material;


    AbstractGeometry(Material material) {
        this.material = material;
    }


    @Override
    public Material getMaterial() {
        return material;
    }


    public void setMaterial(Material material) {
        this.material = material;
    }
}
