package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.main.ChromaLogger;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.geometry.Geometry;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author bensteinert
 */
public class BvhTreeBuilder {

    private static final Logger LOGGER = ChromaLogger.get();
    private Geometry[] primitives;
    private int[] indices;
    private AxisAlignedBoundingBox[] boxes;
    private Vector3[] centroids;
    private int bvhQualityIndex = 0;

    private int maxTreeDepth;
    private int minIndices;


    public BvhTreeBuilder(int trianglesPerNode, int maxTreeDepth) {
        this.minIndices = trianglesPerNode;
        this.maxTreeDepth = maxTreeDepth;
    }


    public BvhTree buildBvh(List<Geometry> geometryList, BvhStrategyType strategy) {

        int totalNumberOfPrimitives = geometryList.size();

        // TODO-IMP: Geometry not yet aligned in memory: Possible improvement with  https://github.com/ObjectLayout/ObjectLayout ?
        primitives = new Geometry[totalNumberOfPrimitives];
        indices = new int[totalNumberOfPrimitives];
        boxes = new AxisAlignedBoundingBox[totalNumberOfPrimitives];
        centroids = new Vector3[totalNumberOfPrimitives];


        for (int i = 0; i < totalNumberOfPrimitives; i++) {
            Geometry element = geometryList.get(i);
            primitives[i] = element;
            indices[i] = i;
            boxes[i] = createBoundingBox(i);
            centroids[i] = boxes[i].getCenter();
        }

        BvhNode root = null;
        bvhQualityIndex = 0;

        switch (strategy) {
            case TOP_DOWN:
                root = createNode(0, totalNumberOfPrimitives - 1);
                buildTreeTopDown(root, 0, 0, totalNumberOfPrimitives - 1);
                break;
            case BOTTOM_UP:
                throw new RuntimeException("Bottom-UP Bvh construction not yet implemented");
        }

        LOGGER.info("Finished BVH Build with a total score of " + bvhQualityIndex);
        return new BvhTree(primitives, root);
    }


    private void buildTreeTopDown(BvhNode node, int depth, int left, int right) {
        int numberOfIndices = right - left + 1;
        if (numberOfIndices <= minIndices || depth == maxTreeDepth) {
            node.indexList = Arrays.copyOfRange(indices, left, right + 1);
            LOGGER.info("Reaching BVH stop criteria with " + numberOfIndices + " indices at depth " + depth);
            return;
        }

        int[] splitIndex = {0, 0, 0};  // the index of the first element to the right of the split plane per axis

        int[] numIndicesLeft = {0, 0, 0};
        int[] numIndicesRight = {0, 0, 0};
        float[] leftBoxVolume = {0, 0, 0};
        float[] rightBoxVolume = {0, 0, 0};
        MutableVector3 boxOverlapVolume = new MutableVector3();

        int[] score = {0, 0, 0};

        Vector3 boxCenter = node.box.getCenter();


        for (int splitAxis = 0; splitAxis < 3; splitAxis++) {
            splitIndex[splitAxis] = partitionIndicesWithPivotAdjusting(splitAxis, boxCenter.getScalar(splitAxis), left, right);

            numIndicesLeft[splitAxis] = splitIndex[splitAxis] - left;
            AxisAlignedBoundingBox leftBox = createBoundingBox(left, splitIndex[splitAxis] - 1);
            leftBoxVolume[splitAxis] = leftBox.getVolume();

            numIndicesRight[splitAxis] = right - splitIndex[splitAxis] + 1;
            AxisAlignedBoundingBox rightBox = createBoundingBox(splitIndex[splitAxis], right);
            rightBoxVolume[splitAxis] = rightBox.getVolume();

            boxOverlapVolume.setValue(splitAxis, leftBox.getOverlapVolume(rightBox));
        }

        // *** Scoring: ***
        ImmutableVector3 volumes = new ImmutableVector3(
                leftBoxVolume[0] + rightBoxVolume[0],
                leftBoxVolume[1] + rightBoxVolume[1],
                leftBoxVolume[2] + rightBoxVolume[2]);

        // split axis with largest extent scores:
        score[node.box.getExtent().getMaxValueIndex()]++;
        // penalty for axis with smalled extent:
        score[node.box.getExtent().getMinValueIndex()]--;


        // split axis which minimizes bounding box volume scores:
        score[volumes.getMinValueIndex()]++;
        // penalty for largest resulting volume
        score[volumes.getMaxValueIndex()]--;

        // split with smallest resulting box overlap scores
        score[boxOverlapVolume.getMinValueIndex()]++;
        // penalty for the axis with highest overlap
        score[boxOverlapVolume.getMaxValueIndex()]--;

        for (int splitAxis = 0; splitAxis < 3; splitAxis++) {
            // unbalanced split axis gets penalty -1
            if (numIndicesLeft[splitAxis] > 2 * numIndicesRight[splitAxis] ||
                    numIndicesRight[splitAxis] > 2 * numIndicesLeft[splitAxis]) {
                score[splitAxis]--;
            }

            // highly unbalanced split axis gets extra penalty (-2)
            // TODO could be made depth dependent:
            if (numIndicesLeft[splitAxis] > 3 * numIndicesRight[splitAxis] ||
                    numIndicesRight[splitAxis] > 3 * numIndicesLeft[splitAxis]) {
                score[splitAxis]--;
            }

            if (numIndicesLeft[splitAxis] == 0 || numIndicesRight[splitAxis] == 0) {
                score[splitAxis]-=10;
            }
        }

        int winnerAxis = new MutableVector3(score[0], score[1], score[2]).getMaxValueIndex();
        bvhQualityIndex += score[winnerAxis];
        int firstRightIndex = partitionIndicesWithPivotAdjusting(winnerAxis, boxCenter.getScalar(winnerAxis), left, right);
        node.left = createNode(left, firstRightIndex - 1);
        node.right = createNode(firstRightIndex, right);
        node.axis = winnerAxis;
        buildTreeTopDown(node.left, depth + 1, left, firstRightIndex - 1);
        buildTreeTopDown(node.right, depth + 1, firstRightIndex, right);
    }


    private int partitionIndicesWithPivotAdjusting(int splitAxisIndex, float pivotValue, int left, int right) {

        assert (right > left);

        int swap;
        int i = left;
        int j = right;
        double centroidSum = pivotValue;
        double tmp;
        int centroidCount = 1;

        while (true) {
            /*
            Let's go through all geometry centroids:
            - Sort them based on their scalar value in the given splitAxis.
            - Additionally sum them up in centroidSum
            - Abort criteria:
              -- All centroids lie the left side of the pivotValue
              -- All centroids lie the right side of the pivotValue
            */
            while ((tmp = (centroids[indices[i]]).getScalar(splitAxisIndex)) <= pivotValue) {
                centroidSum += tmp;
                centroidCount++;
                if (i == right) {
                    return partitionIndices(splitAxisIndex, (centroidSum / centroidCount), left, right); // special case: all tris on the left side
                }
                i++;
            }

            while ((tmp = (centroids[indices[j]]).getScalar(splitAxisIndex)) > pivotValue) {
                centroidSum += tmp;
                centroidCount++;
                j--;
                if (j == left) {
                    break; // special case: all tris on the right
                }
            }

            if (i >= j) {
                return partitionIndices(splitAxisIndex, (centroidSum / centroidCount), left, right);
            }

            // Swap two elements that were identified to lie on the wrong side
            swap = indices[j];
            indices[j] = indices[i];
            indices[i] = swap;
        }
    }


    private int partitionIndices(int splitAxisIndex, double pivot, int left, int right) {

        assert (right > left);

        int tmp;
        int i = left;
        int j = right;

        while (true) {

            while ((centroids[indices[i]]).getScalar(splitAxisIndex) <= pivot) {
                if (i == right) {
                    return right + 1; // special case: all tris on the left side
                }
                i++;
            }

            while ((centroids[indices[j]]).getScalar(splitAxisIndex) > pivot) {
                j--;
                if (j == left) {
                    break; // special case: all tris on the right
                }
            }

            if (i >= j) return i;

            tmp = indices[j];
            indices[j] = indices[i];
            indices[i] = tmp;
        }
    }


    private BvhNode createNode(int leftIdx, int rightIdx) {
        AxisAlignedBoundingBox boundingBox = createBoundingBox(leftIdx, rightIdx);
        return new BvhNode(boundingBox, rightIdx - leftIdx + 1);
    }


    private AxisAlignedBoundingBox createBoundingBox(int leftIdx, int rightIdx) {
        ImmutableVector3 pMin = Vector3.FLT_MAX;
        ImmutableVector3 pMax = Vector3.MINUS_FLT_MAX;
        for (int i = leftIdx; i <= rightIdx; i++) {
            pMin = VectorUtils.minVector(pMin, primitives[indices[i]].getSpatialMinimum());
            pMax = VectorUtils.maxVector(pMax, primitives[indices[i]].getSpatialMaximum());
        }
        return new AxisAlignedBoundingBox(pMin, pMax);
    }


    private AxisAlignedBoundingBox createBoundingBox(int index) {
        return createBoundingBox(index, index);
    }
}

