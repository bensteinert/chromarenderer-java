package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.raytracing.CoordinateSystem;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.scene.Radiance;
import org.apache.commons.math3.util.FastMath;

/**
 * @author bensteinert
 */
public class BlinnPhongShader {

    static Ray getRecursiveRaySample(Ray incomingRay, Hitpoint hitpoint) {
        ImmutableVector3 mirrorDir = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
        return getCosineDistributedLobeSample(mirrorDir, hitpoint, 1);
    }


    static Radiance sampleDirectRadiance(Ray incomingRay, Hitpoint hitpoint) {
        return null;
    }


    static Ray getCosineDistributedLobeSample(ImmutableVector3 direction, Hitpoint hitpoint, int lobeNumber) {

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

        final Ray ray = new Ray(hitpoint.getPoint(), new ImmutableVector3(newDirection));
        float cosTheta = newDirection.dot(hitpoint.getHitpointNormal());

        if (cosTheta < Constants.FLT_EPSILON) {
            ray.setSampleWeight(0.0f);
        } else {
            ray.setSampleWeight(((lobeNumber + 2.0f) / (lobeNumber + 1.0f)) * cosTheta);
        }
        return ray;

    }
}
