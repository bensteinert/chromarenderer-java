package net.chromarenderer.math.geometry;

import net.chromarenderer.YouGotMeException;
import net.chromarenderer.math.*;
import net.chromarenderer.math.raytracing.CoordinateSystem;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.shader.Material;
import org.apache.commons.math3.util.FastMath;

import static net.chromarenderer.math.Constants.TWO_PI_f;

/**
 * @author bensteinert
 */
public class Disk extends AbstractGeometry implements Geometry {

    private final ImmutableVector3 center;
    private final float radius;
    private final float squaredRadius;
    private final ImmutableVector3 normal;


    public Disk(ImmutableVector3 center, float radius, ImmutableVector3 normal) {
        super(Material.NULL);
        this.center = center;
        this.radius = radius;
        this.squaredRadius = radius * radius;
        this.normal = normal;
    }

    @Override
    public float intersect(Ray ray) {
        //general ray equation placed into plane equation:
        //0 = (H-P) * n
        //H = O + t*d
        //0 = (O+t*d-P)*n

        // NO Backface Culling! This means, the Disk behaves solid in both normal and opposite direction!

        float t = PlaneUtils.planeRayIntersection(center, normal, ray);

        if (t > 0.0f) {
            float squaredDist = ray.mutableOnRay(t).minus(center).squaredLength();
            if (squaredDist < squaredRadius) {
                return t;
            }
        }
        return 0.0f;
    }


    @Override
    public Geometry transpose(Vector3 transpose) {
        return new Disk(center.plus(transpose), radius, normal);
    }

    @Override
    public Geometry rotate(ImmutableMatrix3x3 rotationY) {
        throw new YouGotMeException();
    }

    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        return normal;
    }

    @Override
    public boolean isPlane() {
        return true;
    }


    @Override
    public float getArea() {
        return Constants.PI_f * radius * radius;
    }

    @Override
    public ImmutableVector3 getUnifDistrSample() {

        float u = ChromaThreadContext.randomFloatClosedOpen();
        float v = ChromaThreadContext.randomFloatClosedOpen();

        float sqrtU = (float) FastMath.sqrt(u);
        float v2pi = v * TWO_PI_f;

        float sampleX = (float) FastMath.cos(v2pi) * sqrtU * radius;
        float sampleY = (float) FastMath.sin(v2pi) * sqrtU * radius;
        float sampleZ = 0.0f; // not relevant on the unit disk

        CoordinateSystem coordinateSystem = VectorUtils.buildCoordSystem(normal);

        Vector3 mutable = new MutableVector3(
                coordinateSystem.getT1()).mult(sampleX)
                .plus(coordinateSystem.getT2().mult(sampleY));

        return new ImmutableVector3(mutable.plus(center));
    }

    @Override
    public ImmutableVector3 getSpatialMinimum() {
        throw new YouGotMeException();
        //return center.minus((float)radius, (float)radius, (float)radius);

    }

    @Override
    public ImmutableVector3 getSpatialMaximum() {
        throw new YouGotMeException();
        //return center.plus((float)radius, (float)radius, (float)radius);
    }

    public ImmutableVector3 getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }

}
