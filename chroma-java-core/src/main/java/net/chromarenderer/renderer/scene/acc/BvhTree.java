package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.AccStructType;
import net.chromarenderer.math.geometry.Geometry;

/**
 * @author bensteinert
 */
public class BvhTree implements AccelerationStructure {

    private final Geometry[] geometry;
    private final BvhNode rootNode;


    public BvhTree(Geometry[] primitives, BvhNode root) {
        this.geometry = primitives;
        rootNode = root;
    }


    @Override
    public void intersect(IntersectionContext ctx) {
        rootNode.intersect(ctx, geometry);
    }


    @Override
    public AccStructType getType() {
        return AccStructType.AABB_BVH;
    }

}
