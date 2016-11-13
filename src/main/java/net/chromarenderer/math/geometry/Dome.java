package net.chromarenderer.math.geometry;

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

    private final ImmutableVector3 center;
    private final ImmutableVector3 domeVector;
    private final float baseRadius;
    private final float curveRadius;
    private final float height;
    private final float cosSemiAngle;

    public Dome(ImmutableVector3 center, ImmutableVector3 domeVector, float baseRadius, float signedCurveRadius) {
        super(Material.NULL);
        this.center = center;

        // ported from chroma1. Doesn't make sense so far ;)
        this.domeVector = new ImmutableVector3(0, 0, (signedCurveRadius > 0 ? 1.0f : -1.0f) * -1);
        this.baseRadius = baseRadius;
        this.curveRadius = FastMath.abs(signedCurveRadius);
        this.height = (float) (curveRadius - FastMath.sqrt(curveRadius * curveRadius - baseRadius * baseRadius));
        this.cosSemiAngle = (curveRadius - height) / curveRadius;
    }

    public Dome(ImmutableVector3 center, ImmutableVector3 domeVector, float baseRadius, float curveRadius, float height, float cosSemiAngle, Material material) {
        super(material);
        this.center = center;
        this.domeVector = domeVector;
        this.baseRadius = baseRadius;
        this.curveRadius = curveRadius;
        this.height = height;
        this.cosSemiAngle = cosSemiAngle;
    }

    @Override
    public float intersect(Ray ray) {
        return 0;
    }

    @Override
    public Geometry transpose(Vector3 transpose) {
        return new Dome(center.plus(transpose), domeVector, baseRadius, curveRadius, height, cosSemiAngle, getMaterial());
    }

    @Override
    public Geometry rotate(ImmutableMatrix3x3 rotationY) {
        throw new YouGotMeException();
    }

    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        // This normal always points away from the way dome center.
        return hitpoint.minus(center).div(curveRadius);
    }

    @Override
    public boolean isPlane() {
        return false;
    }


    @Override
    public float getArea() {
        return TWO_PI_f * height * curveRadius;
    }

    @Override
    public ImmutableVector3 getUnifDistrSample() {
        Vector3 t1, t2;

        CoordinateSystem coordinateSystem = VectorUtils.buildCoordSystem(domeVector);

        /*
        * Cap sampling
        * from Global Illumination Compendium p19f
        * pdf is 1/(2Pi*(1-costhetamax))
        */

        float u = (float) ChromaThreadContext.randomDoubleClosedOpen();
        float v = (float) ChromaThreadContext.randomDoubleClosedOpen();
        float sqrtTerm = (float) Math.sqrt(1.0f - Math.pow(1.0f - u * (1.0f - cosSemiAngle), 2.0f));
        float v2pi = v * TWO_PI_f;

        float sampleX = sqrtTerm * (float) FastMath.cos(v2pi) * curveRadius;
        float sampleY = sqrtTerm * (float) FastMath.sin(v2pi) * curveRadius;
        float sampleZ = 1.0f - u * (1.0f - cosSemiAngle) * curveRadius;

        // rotate
        Vector3 mutable = new MutableVector3(
                coordinateSystem.getT1()).mult(sampleX)
                .plus(coordinateSystem.getT2().mult(sampleY))
                .plus(coordinateSystem.getN().mult(sampleZ)).normalize();

        // transpose
        return new ImmutableVector3(mutable.plus(center));
    }

    @Override
    public ImmutableVector3 getSpatialMinimum() {
        return null;
    }

    @Override
    public ImmutableVector3 getSpatialMaximum() {
        return null;
    }
}
