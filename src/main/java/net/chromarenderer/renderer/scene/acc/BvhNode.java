package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.geometry.Geometry;

import java.util.Arrays;
import java.util.logging.Logger;

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
            Arrays.stream(indexList).forEach(idx -> {
                if (geometry[idx] != ctx.hitGeometry) {
                    ctx.checkGeometry(geometry[idx]);
                } else {
                    Logger.getGlobal().warning("re-intersection prevented.");
                }
            });
        } else {
            float tMinLeft;
            float tMinRight;

            int hitBits = ctx.intersectionMode;

            hitBits |= left.box.intersects(ctx);
            tMinLeft = ctx.lastTValues[0];

            hitBits = hitBits | right.box.intersects(ctx) << 1;
            tMinRight = ctx.lastTValues[0];

            switch (hitBits) {
                case 1: // left hit
                case 5: // left hit with any mode
                    left.intersect(ctx, geometry);
                    break;
                case 2: // right hit
                case 6: // right hit with any mode
                    right.intersect(ctx, geometry);
                    break;
                case 3: // both hit first mode
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
                case 7: // both hit with any mode
                    if (tMinLeft <= tMinRight) {
                        left.intersect(ctx, geometry);
                        if (ctx.hitGeometry == null) {
                            right.intersect(ctx, geometry);
                        }

                    } else {
                        right.intersect(ctx, geometry);
                        if (ctx.hitGeometry == null) {
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
