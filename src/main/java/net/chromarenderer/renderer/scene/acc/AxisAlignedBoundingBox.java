package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;

/**
 * @author bensteinert
 */
public class AxisAlignedBoundingBox {

    private final ImmutableVector3 pMin;
    private final ImmutableVector3 pMax;

    public AxisAlignedBoundingBox(ImmutableVector3 pMin, ImmutableVector3 pMax) {
        this.pMin = pMin;
        this.pMax = pMax;
    }

    public ImmutableVector3 getCenter() {
        return pMin.plus(pMax.minus(pMin).mult(0.5f));
    }

    public boolean intersects(IntersectionContext ctx) {

        float xmin, xmax, ymin, ymax, zmin, zmax;
        Vector3 bounds[] = new Vector3[]{pMin, pMax};
        Ray ray = ctx.ray;
        ImmutableVector3 invDirection = ray.getInvDirection();
        ImmutableVector3 origin = ray.getOrigin();

        xmin = (bounds[ray.getXSign()].getX() - origin.getX()) * invDirection.getX();
        xmax = (bounds[1 - ray.getXSign()].getX() - origin.getX()) * invDirection.getX();
        ymin = (bounds[ray.getSignY()].getY() - origin.getY()) * invDirection.getY();
        ymax = (bounds[1 - ray.getSignY()].getY() - origin.getY()) * invDirection.getY();

        if ((xmin > ymax) || (ymin > xmax))
            return false;

        if (ymin > xmin) {
            xmin = ymin;
        }
        if (ymax < xmax) {
            xmax = ymax;
        }

        zmin = (bounds[ray.getSignZ()].getZ() - origin.getZ()) * invDirection.getZ();
        zmax = (bounds[1 - ray.getSignZ()].getZ() - origin.getZ()) * invDirection.getZ();

        if ((xmin > zmax) || (zmin > xmax)) {
            return false;
        }

        if (zmin > xmin) {
            xmin = zmin;
        }
        if (zmax < xmax) {
            xmax = zmax;
        }

        return (xmin < ray.getTMax()) && (xmax > ray.getTMin());
    }
}
