package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.geometry.Geometry;

import java.util.Arrays;

/**
 * @author bensteinert
 */
class BvhNode {

    AxisAlignedBoundingBox box;
    BvhNode left;
    BvhNode right;
    int axis;
    int[] indexList;


    BvhNode(AxisAlignedBoundingBox box, int axis) {
        this.box = box;
        this.axis = axis;
        this.indexList = null;
    }


    void intersect(IntersectionContext ctx, Geometry[] geometry) {
        if (indexList != null) {
            Arrays.stream(indexList).forEach(idx -> ctx.checkGeometry(geometry[idx]));
        } else {
            // Naive: one can be avoided if we take ray direction into account.
            //left.intersect(ctx, geometry);
            //right.intersect(ctx, geometry);

            float tMinLeft;
            float tMinRight;

            int hitBits = left.box.intersects(ctx);
            tMinLeft = ctx.lastTValues[0];

            hitBits = hitBits | right.box.intersects(ctx) << 1;
            tMinRight = ctx.lastTValues[0];

            switch (hitBits) {
                case 1: // left hit
                    left.intersect(ctx, geometry);
                    break;
                case 2: // right hit
                    right.intersect(ctx, geometry);
                    break;
                case 3: // both hit
                    if (tMinLeft <= tMinRight) {
                        left.intersect(ctx, geometry);
                        if (ctx.hitDistance > tMinRight) {
                            right.intersect(ctx, geometry);
                        }
                    } else {
                        right.intersect(ctx, geometry);
                        if (ctx.hitDistance > tMinLeft) {
                            left.intersect(ctx, geometry);
                        }
                    }
                    break;
            }
        }
    }


    @Override
    public String toString() {
        return "BvhNode{" +
                ", box=" + box +
                '}';
    }
}
