package net.chroma.math.raytracing;

import net.chroma.math.ImmutableVector3;
import net.chroma.math.Vector3;
import net.chroma.math.geometry.Geometry;

/**
 * @author steinerb
 */
public class Hitpoint {

    public static final Hitpoint INFINITY = new Hitpoint(null, null, Float.MAX_VALUE, null);

    private final Geometry hitGeometry;
    private final ImmutableVector3 point;
    private final float distance;
    private final Vector3 hitpointNormal;


    public Hitpoint(Geometry hitGeometry, ImmutableVector3 point, float distance, Vector3 hitpointNormal) {
        this.hitGeometry = hitGeometry;
        this.point = point;
        this.distance = distance;
        this.hitpointNormal = hitpointNormal;
    }


    public ImmutableVector3 getPoint() {
        return point;
    }


    public float getDistance() {
        return distance;
    }


    public Vector3 getHitpointNormal() {
        return hitpointNormal;
    }


    public Geometry getHitGeometry() {
        return hitGeometry;
    }


    public boolean hit(){
        return hitGeometry != null;
    }
}
