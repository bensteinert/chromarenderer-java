package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.geometry.Geometry;

import java.util.Arrays;
import java.util.List;

/**
 * @author bensteinert
 */
public class BvhTreeBuilder {

    private Geometry[] primitives;
    private int[] indices;
    private AxisAlignedBoundingBox[] boxes;
    private Vector3[] centroids;

    public BvhTree buildBvh(List<Geometry> geometryList, int maxTreeDepth, int minTriangles) {

        int totalNumberOfPrimitives = geometryList.size();

        // TODO-IMP: Geometry not yet aligned in memory: Possible improvement with  https://github.com/ObjectLayout/ObjectLayout
        primitives = new Geometry[totalNumberOfPrimitives];
        indices = new int[totalNumberOfPrimitives];
        boxes = new AxisAlignedBoundingBox[totalNumberOfPrimitives];
        centroids = new Vector3[totalNumberOfPrimitives];


        for (int i = 0; i < totalNumberOfPrimitives; i++) {
            Geometry element = geometryList.get(i);
            primitives[i] = element;
            indices[i] = i;
            boxes[i] = buildBoundingBox(element);
            centroids[i] = boxes[i].getCenter();
        }

        BvhNode root = createNode(0, totalNumberOfPrimitives - 1);
        return new BvhTree(primitives, root);
    }

    private BvhNode createNode(int leftIdx, int rightIdx) {
        ImmutableVector3 pMin = Vector3.FLT_MAX;
        ImmutableVector3 pMax = Vector3.FLT_MIN;
        for (int i = leftIdx; i <= rightIdx; i++) {
            pMin = VectorUtils.minVector(pMin, primitives[i].getSpatialMinimum());
            pMax = VectorUtils.maxVector(pMax, primitives[i].getSpatialMaximum());
        }

        return new BvhNode(new AxisAlignedBoundingBox(pMin, pMax), -1, rightIdx - leftIdx, Arrays.copyOfRange(indices, leftIdx, rightIdx));
    }

    private static AxisAlignedBoundingBox buildBoundingBox(Geometry element) {
        ImmutableVector3 pMin = element.getSpatialMinimum();
        ImmutableVector3 pMax = element.getSpatialMaximum();
        return new AxisAlignedBoundingBox(pMin, pMax);
    }
}
