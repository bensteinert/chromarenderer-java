package net.chromarenderer.renderer.scene.acc;

/**
 * Created by ben on 07/03/16.
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
}
