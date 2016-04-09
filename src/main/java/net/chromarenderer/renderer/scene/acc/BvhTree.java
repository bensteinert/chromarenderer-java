package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.geometry.Geometry;

/**
 * @author bensteinert
 */
public class BvhTree implements AccelerationStructure {

    private final Geometry[] primitives;
    private final BvhNode rootNode;


    public BvhTree(Geometry[] primitives, BvhNode root) {
        this.primitives = primitives;
        rootNode = root;
    }


    @Override
    public void intersect(IntersectionContext ctx) {
        BvhNode node = rootNode;

        if(node.intersects(ctx)) {
            for (int i = 0; i < node.numberOfIndices ; i++) {
                Geometry geometry = primitives[node.indexList[i]];
                ctx.checkGeometry(geometry);
            }
        }
    }


}
