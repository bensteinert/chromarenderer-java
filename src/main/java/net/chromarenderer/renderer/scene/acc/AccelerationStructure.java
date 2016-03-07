package net.chromarenderer.renderer.scene.acc;


/**
 * Created by ben on 06/03/16.
 */
public interface AccelerationStructure {
    void intersect(IntersectionContext ctx);
}
