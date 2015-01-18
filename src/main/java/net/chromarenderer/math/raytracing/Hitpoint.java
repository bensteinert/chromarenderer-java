package net.chromarenderer.math.raytracing;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Geometry;

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

    public CoordinateSystem getCoordinateSystem () {
        MutableVector3 t1 =  new MutableVector3(hitpointNormal);
        t1.setValue(t1.getMinIndexAbs(), 1.0f);
        t1.crossProduct(hitpointNormal);
        t1.normalize();
        Vector3 t2 = hitpointNormal.crossProduct(t1).normalize();
        return new CoordinateSystem(new ImmutableVector3(t1), t2, hitpointNormal);
    }


    public boolean hit(){
        return hitGeometry != null;
    }
}
