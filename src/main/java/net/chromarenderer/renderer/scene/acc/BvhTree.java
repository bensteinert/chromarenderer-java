package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.Vector3;
import net.chromarenderer.renderer.scene.GeometryScene;

/**
 * Created by ben on 07/03/16.
 */
public class BvhTree implements AccelerationStructure {

    private BvhNode rootNode;

    private int maxDepth;
    private int minTriangles;
    private AxisAlignedBoundingBox[] allBoxes;
    private Vector3[] centroids;



    @Override
    public void intersect(IntersectionContext ctx) {

    }


}
