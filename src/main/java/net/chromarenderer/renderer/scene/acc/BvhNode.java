package net.chromarenderer.renderer.scene.acc;

/**
 * @author bensteinert
 */
public class BvhNode {

    private AxisAlignedBoundingBox box;
    private BvhNode left;
    private BvhNode right;
    int numberOfIndices;
    int axis;
    int[] indexList;


    public BvhNode(AxisAlignedBoundingBox box, int axis, int numberOfIndices, int[] indexList) {
        this.box = box;
        this.axis = axis;
        this.numberOfIndices = numberOfIndices;
        this.indexList = indexList;
    }


    public boolean intersects(IntersectionContext ctx) {
        return box.intersects(ctx);
    }
}
