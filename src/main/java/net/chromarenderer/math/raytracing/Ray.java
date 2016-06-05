package net.chromarenderer.math.raytracing;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.geometry.Geometry;

/**
 * @author steinerb
 */
public class Ray {

    private final ImmutableVector3 origin;
    private final ImmutableVector3 direction;
    private final ImmutableVector3 invDirection;
    private final float tMin;
    private final float tMax;
    private float sampleWeight = 1.0f;

    // if the direction is negative, sign =
    private final byte signX;
    private final byte signY;
    private final byte signZ;
    private Geometry lastHitGeomerty;


    public Ray(ImmutableVector3 origin, ImmutableVector3 direction) {
        this(origin, direction, Constants.FLT_EPSILON, Float.MAX_VALUE);
    }


    public Ray(ImmutableVector3 origin, ImmutableVector3 direction, float tMin, float tMax) {
        this.origin = origin;
        this.direction = direction;
        invDirection = new ImmutableVector3(1.f / direction.getX(), 1.f / direction.getY(), 1.f / direction.getZ());
        this.tMin = tMin;
        this.tMax = tMax;
        this.signX = (byte) (invDirection.getX() < 0 ? 1 : 0);
        this.signY = (byte) (invDirection.getY() < 0 ? 1 : 0);
        this.signZ = (byte) (invDirection.getZ() < 0 ? 1 : 0);
    }


    public ImmutableVector3 getOrigin() {
        return origin;
    }


    public ImmutableVector3 getDirection() {
        return direction;
    }


    public ImmutableVector3 getInvDirection() {
        return invDirection;
    }

    public ImmutableVector3 getBackwardsDirection() {
        return direction.mult(-1.0f);
    }


    public float getTMin() {
        return tMin;
    }


    public float getTMax() {
        return tMax;
    }


    public ImmutableVector3 onRay(float t) {
        return origin.plus(direction.mult(t));
    }


    public boolean isOnRay(float distance) {
        return distance > getTMin() && distance <= getTMax();
    }


    public byte getXSign() {
        return signX;
    }


    public byte getSignY() {
        return signY;
    }


    public byte getSignZ() {
        return signZ;
    }


    public float getInverseSampleWeight() {
        return sampleWeight;
    }


    public void setInverseSampleWeight(float sampleWeight) {
        this.sampleWeight = sampleWeight;
    }


    public void mailbox(Geometry geometry) {
        lastHitGeomerty = geometry;
    }


    public Geometry getLastHitGeomerty() {
        return lastHitGeomerty;
    }
}
