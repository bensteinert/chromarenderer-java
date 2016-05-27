package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.raytracing.CoordinateSystem;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;
import org.apache.commons.math3.util.FastMath;

/**
 * @author bensteinert
 */
public class BlinnPhongShader {

    static ChromaScene scene;

    static Ray getRecursiveRaySample(Ray incomingRay, Hitpoint hitpoint) {
        final int lobeNumber = 20;
        ImmutableVector3 mirrorDir = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
        final ImmutableVector3 newDirection = getCosineDistributedLobeSample(mirrorDir, hitpoint, lobeNumber);
        float cosTheta = newDirection.dot(hitpoint.getHitpointNormal());
        final Ray result = new Ray(hitpoint.getPoint(), new ImmutableVector3(newDirection));

        if (cosTheta < Constants.FLT_EPSILON) {
            result.setSampleWeight(0.0f);
        } else {
            result.setSampleWeight(((lobeNumber + 2.0f) / (lobeNumber + 1.0f)) * cosTheta);
        }
        return result;
    }


    static Radiance sampleDirectRadiance(Ray incomingRay, Hitpoint hitpoint) {
        final Ray sampledDirection = getRecursiveRaySample(incomingRay, hitpoint);
        if(sampledDirection.getSampleWeight() > Constants.FLT_EPSILON) {
            Hitpoint lightSourceSample = scene.intersect(sampledDirection);
            if (lightSourceSample.isOn(MaterialType.EMITTING)) {
                Material emitting = lightSourceSample.getHitGeometry().getMaterial();
                return new Radiance(emitting.getEmittance().mult(1.0f - Constants.FLT_EPSILON), sampledDirection);
            }
        }

        return new Radiance(COLORS.BLACK, sampledDirection);
    }

    /**
     * Produces a cosine distributed ray sampled within a lobe defined by lobeNumber around mirror direction.
     * @param direction incoming direction
     * @param hitpoint hitpoint on the surface to shade
     * @param lobeNumber characteristic
     * @return new sampled ray.
     */
    static ImmutableVector3 getCosineDistributedLobeSample(ImmutableVector3 direction, Hitpoint hitpoint, int lobeNumber) {

        final CoordinateSystem coordinateSystem = VectorUtils.buildCoordSystem(direction);
        float u = ChromaThreadContext.randomFloatClosedOpen();
        float v = ChromaThreadContext.randomFloatClosedOpen();

        float temp = (float) FastMath.sqrt(1.0f - FastMath.pow(u, (2.0f / (lobeNumber + 1))));
        float v2pi = v * Constants.TWO_PI_f;

        // now sample along normal with wished lobe. Rotate with angles of mirror_dir an make cartesian dir
        final float sampleX = (float) (FastMath.cos(v2pi) * temp);
        final float sampleY = (float) (FastMath.sin(v2pi) * temp);
        final float sampleZ = (float) FastMath.pow(u, 1.0f / (lobeNumber + 1));
        Vector3 newDirection = new MutableVector3(coordinateSystem.getT1()).mult(sampleX)
                .plus(coordinateSystem.getT2().mult(sampleY))
                .plus(coordinateSystem.getN().mult(sampleZ)).normalize();

        return new ImmutableVector3(newDirection);
    }
}
