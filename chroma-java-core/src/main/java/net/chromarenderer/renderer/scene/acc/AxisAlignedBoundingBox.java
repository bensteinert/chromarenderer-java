package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.raytracing.Ray;
import org.apache.commons.math3.util.FastMath;

/**
 * @author bensteinert
 */
public class AxisAlignedBoundingBox {

    private final ImmutableVector3 bounds[];


    public AxisAlignedBoundingBox(ImmutableVector3 pMin, ImmutableVector3 pMax) {
        bounds = new ImmutableVector3[]{pMin, pMax};
    }


    public ImmutableVector3 getPMin() {
        return bounds[0];
    }


    public ImmutableVector3 getPMax() {
        return bounds[1];
    }


    public ImmutableVector3 getCenter() {
        return bounds[0].plus(bounds[1].minus(bounds[0]).mult(0.5f));
    }


    public int intersects(IntersectionContext ctx) {

        float xmin, xmax, ymin, ymax, zmin, zmax;

        Ray ray = ctx.ray;
        ImmutableVector3 invDirection = ray.getInvDirection();
        ImmutableVector3 origin = ray.getOrigin();

        xmin = (bounds[ray.getXSign()].getX() - origin.getX()) * invDirection.getX();
        xmax = (bounds[1 - ray.getXSign()].getX() - origin.getX()) * invDirection.getX();
        ymin = (bounds[ray.getSignY()].getY() - origin.getY()) * invDirection.getY();
        ymax = (bounds[1 - ray.getSignY()].getY() - origin.getY()) * invDirection.getY();

        if ((xmin > ymax) || (ymin > xmax)) {
            return 0;
        }

        if (ymin > xmin) {
            xmin = ymin;
        }
        if (ymax < xmax) {
            xmax = ymax;
        }

        zmin = (bounds[ray.getSignZ()].getZ() - origin.getZ()) * invDirection.getZ();
        zmax = (bounds[1 - ray.getSignZ()].getZ() - origin.getZ()) * invDirection.getZ();

        if ((xmin > zmax) || (zmin > xmax)) {
            return 0;
        }

        if (zmin > xmin) {
            xmin = zmin;
        }
        if (zmax < xmax) {
            xmax = zmax;
        }

        if ((xmin < ray.getTMax()) && (xmax > ray.getTMin())) {
            ctx.lastTValues[0] = xmin > ray.getTMin() ? xmin : ray.getTMin();
            ctx.lastTValues[1] = xmax < ray.getTMax() ? xmax : ray.getTMax();
            return 1;
        } else {
            return 0;
        }
    }


    @Override
    public String toString() {
        return "AxisAlignedBoundingBox{" +
                "pMin=" + getPMin() +
                ", pMax=" + getPMax() +
                '}';
    }


    public ImmutableVector3 getExtent() {
        return getPMax().minus(getPMin());
    }


    public float getVolume() {
        ImmutableVector3 extent = getExtent();
        return FastMath.abs(extent.getX() * extent.getY() * extent.getZ());
    }


    public float getOverlapVolume(AxisAlignedBoundingBox otherBox) {
        if (otherBox.getPMin().isCloserToOriginThan(this.getPMin())) {
            // swap for checking ...
            return otherBox.getOverlapVolume(this);
        } else {
            if (otherBox.getPMin().isCloserToOriginThan(this.getPMax())) {
                return new AxisAlignedBoundingBox(otherBox.getPMin(), this.getPMax()).getVolume();
            } else {
                return 0.f;
            }
        }
    }
}
