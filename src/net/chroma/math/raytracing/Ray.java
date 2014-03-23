package net.chroma.math.raytracing;

import net.chroma.math.Constants;
import net.chroma.math.ImmutableVector3;

/**
 * @author steinerb
 */
public class Ray {

    private final ImmutableVector3 origin;
    private final ImmutableVector3 direction;
    private final ImmutableVector3 invDirection;
    private final float tMin;
    private final float tMax;

    public Ray(ImmutableVector3 origin, ImmutableVector3 direction) {
        this(origin, direction, Constants.FLT_EPSILON, Float.MAX_VALUE);
    }

    public Ray(ImmutableVector3 origin, ImmutableVector3 direction, float tMin, float tMax) {
        this.origin = origin;
        this.direction = direction;
        invDirection =  new ImmutableVector3(1.f / direction.getX(), 1.f / direction.getY(), 1.f / direction.getZ());
        this.tMin = tMin;
        this.tMax = tMax;
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

    public float getTMin() {
        return tMin;
    }

    public float getTMax() {
        return tMax;
    }

    public ImmutableVector3 onRay(float t){
        return origin.plus(direction.mult(t));
    }

    public boolean isOnRay(float distance) {
        return distance > getTMin() && distance <= getTMax();
    }
}
