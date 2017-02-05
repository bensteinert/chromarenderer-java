package net.chromarenderer.renderer.scene.acc;


import net.chromarenderer.AccStructType;

/**
 * @author bensteinert
 */
public interface AccelerationStructure {
    void intersect(IntersectionContext ctx);

    AccStructType getType();
}
