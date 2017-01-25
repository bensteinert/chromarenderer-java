package net.chromarenderer.renderer.scene.acc;


/**
 * @author bensteinert
 */
public interface AccelerationStructure {
    void intersect(IntersectionContext ctx);

    AccStructType getType();
}
