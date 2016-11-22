package net.chromarenderer.math.geometry;

import net.chromarenderer.YouGotMeException;
import net.chromarenderer.main.ChromaStatistics;
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
public class Dome extends AbstractGeometry implements Geometry {

    private final ImmutableVector3 sphereCenter;
    private final float sphereRadius;

    private final ImmutableVector3 domeOrientation;
    private final float domeBaseRadius;
    private final float domeHeight;
    private final float cosSemiAngle;

    private Dome(ImmutableVector3 sphereCenter, float sphereRadius, ImmutableVector3 domeOrientation, float domeBaseRadius, float domeHeight, float cosSemiAngle, Material material) {
        super(material);
        this.sphereCenter = sphereCenter;
        this.domeOrientation = domeOrientation;
        this.domeBaseRadius = domeBaseRadius;
        this.sphereRadius = sphereRadius;
        this.domeHeight = domeHeight;
        this.cosSemiAngle = cosSemiAngle;
    }


    public Dome(ImmutableVector3 sphereCenter, float sphereRadius, ImmutableVector3 domeOrientation, float domeHeight, Material material) {
        super(material);
        this.sphereCenter = sphereCenter;
        this.sphereRadius = sphereRadius;
        this.domeOrientation = domeOrientation;
        this.domeHeight = domeHeight;
        this.domeBaseRadius = (float) FastMath.sqrt(domeHeight * (2*sphereRadius -domeHeight));
        this.cosSemiAngle = (sphereRadius - domeHeight) / sphereRadius;
    }


    @Override
    public float intersect(Ray ray) {
        ChromaStatistics.intersectOp();

        // TODO: inner/outer hit handling required?

        // TODO: Disk handling needed?
//        if (cosSemiAngle == 1.0f) {
//            throw new UnexpectedGeometryException("Dome which degraded to a Disk.");
//        }

        //code from http://wiki.cgsociety.org/index.php/Ray_Sphere_Intersection
        //special intersect routine returning both t values for later analysis eg in lens intersection test

        ImmutableVector3 OminusC = ray.getOrigin().minus(sphereCenter);

        double a = ray.getDirection().dot(ray.getDirection());
        double b = ray.getDirection().dot(OminusC) * 2.0;
        double c = OminusC.dot(OminusC) - sphereRadius * sphereRadius;

        //Find discriminant
        double disc = (b * b) - (4.0 * a * c);

        // if discriminant is negative there are no real roots, so return
        // false as ray misses sphere
        if (disc < 0.0) return 0.0f;

        // compute q as described above
        double distSqrt = FastMath.sqrt(disc);
        double q = b < 0 ? (-b - distSqrt) * 0.5 : (-b + distSqrt) * 0.5;

        // compute t0 and t1
        float t0 = (float) (q / a);
        float t1 = (float) (c / q);

        // make sure t0 is smaller than t1
        if (t0 > t1) {
            float temp = t0;
            t0 = t1;
            t1 = temp;
        }

        // ignore imprecision flaws.....
        if (!(t0 < Constants.SPHERE_NAN_LIMIT) || !(t1 < Constants.SPHERE_NAN_LIMIT)) {
            return 0.0f;
        }


        if (t0 > 0.0f) {
            float domeAngle = ray.mutableOnRay(t0).minus(sphereCenter).div(sphereRadius).dot(domeOrientation);
            // hit t0 inside dome angle
            if (domeAngle > cosSemiAngle) {
                return t0;
            }
        }
        else {
            float domeAngle = ray.mutableOnRay(t1).minus(sphereCenter).div(sphereRadius).dot(domeOrientation);
            // hit t1 inside dome angle
            if (domeAngle > cosSemiAngle) {
                return t1;
            }
        }

        return 0.0f;
    }

    @Override
    public Geometry transpose(Vector3 transpose) {
        return new Dome(sphereCenter.plus(transpose), sphereRadius, domeOrientation, domeBaseRadius, domeHeight, cosSemiAngle, getMaterial());
    }

    @Override
    public Geometry rotate(ImmutableMatrix3x3 rotationY) {
        throw new YouGotMeException();
    }

    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        // TODO: This normal always points away from the way dome sphereCenter. We might need the other direction as well. Think of concave lenses
        return hitpoint.minus(sphereCenter).div(sphereRadius);
    }

    @Override
    public boolean isPlane() {
        return false;
    }


    @Override
    public float getArea() {
        return TWO_PI_f * domeHeight * sphereRadius;
    }

    @Override
    public ImmutableVector3 getUnifDistrSample() {

        CoordinateSystem coordinateSystem = VectorUtils.buildCoordSystem(domeOrientation);

        /*
        * Cap sampling
        * from Global Illumination Compendium p19f
        * pdf is 1/(2Pi*(1-cosSemiAngle))
        */

        float u = (float) ChromaThreadContext.randomDoubleClosedOpen();
        float v = (float) ChromaThreadContext.randomDoubleClosedOpen();
        float sqrtTerm = (float) FastMath.sqrt(1.0f - FastMath.pow(1.0f - u * (1.0f - cosSemiAngle), 2.0f));
        float v2pi = v * TWO_PI_f;

        float sampleX = sqrtTerm * (float) FastMath.cos(v2pi) * sphereRadius;
        float sampleY = sqrtTerm * (float) FastMath.sin(v2pi) * sphereRadius;
        float sampleZ = 1.0f - u * (1.0f - cosSemiAngle) * sphereRadius;

        // rotate
        Vector3 mutable = new MutableVector3(
                coordinateSystem.getT1()).mult(sampleX)
                .plus(coordinateSystem.getT2().mult(sampleY))
                .plus(coordinateSystem.getN().mult(sampleZ));

        // transpose and return
        return new ImmutableVector3(mutable.plus(sphereCenter));
    }

    @Override
    public ImmutableVector3 getSpatialMinimum() {
        throw new YouGotMeException();
    }

    @Override
    public ImmutableVector3 getSpatialMaximum() {
        throw new YouGotMeException();
    }

    public ImmutableVector3 getSphereCenter() {
        return sphereCenter;
    }


    public float getSphereRadius() {
        return sphereRadius;
    }


    public ImmutableVector3 getDomeOrientation() {
        return domeOrientation;
    }


    public float getDomeHeight() {
        return domeHeight;
    }


    public float getCosSemiAngle() {
        return cosSemiAngle;
    }


    public float getDomeBaseRadius() {
        return domeBaseRadius;
    }
}
