package net.chroma.math.raytracing;

import net.chroma.math.Constants;
import net.chroma.math.ImmutableVector3;

/**
 * @author steinerb
 */
public class Ray {

    private ImmutableVector3 origin;
    private ImmutableVector3 direction;
    private ImmutableVector3 invDirection;

    public Ray(ImmutableVector3 origin, ImmutableVector3 direction) {
        this.origin = origin;
        this.direction = direction;
        invDirection =  new ImmutableVector3(1.f / direction.getX(), 1.f / direction.getY(), 1.f / direction.getZ());
    }

    //    float tmin, tmax; // interval				// 8
//    float lambda;   							// 4
//    char sign[3];								// 3
//    bool spectral;


    public ImmutableVector3 getOrigin() {
        return origin;
    }

    public ImmutableVector3 getDirection() {
        return direction;
    }

    public ImmutableVector3 getInvDirection() {
        return invDirection;
    }

    public double getTMin() {
        return Constants.DBL_EPSILON;
    }

    public ImmutableVector3 onRay(float t){
        return origin.plus(direction.mult(t));
    }
}
