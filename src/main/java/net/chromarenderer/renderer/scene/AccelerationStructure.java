package net.chromarenderer.renderer.scene;

/**
 * Created by ben on 06/03/16.
 */
public interface AccelerationStructure {
    void getHitGeometry(GeometryScene.IntersectionContext ctx);
}
