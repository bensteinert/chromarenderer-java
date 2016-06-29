package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
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
class DiffuseShader implements ChromaShader {

    private ChromaScene scene;

    /*
     * Simple path tracing with uniform hemisphere samples to determine path.
     * Ls(ω) = ∫ Li * ρ(ωi, ωo) * cosθ dω
     * <p>
     * MC computation with one sample replaces ∫:
     * Ls(ω) = (Li * ρ(ωi, ωo) * cosθ) / p(ω)
     * <p>
     * where p(ω) = cos(θ)/π (sample weight for a uniform sample on the hemisphere - PT)
     * Ls(ω) = (Li * ρ(ωi, ωo)) * π
     * <p>
     * Why can I neglect *π? (missing in the code)
     * Assume Li = 1, Ls = 0,8: (80% get reflected)
     * 0,8 = ρ(ωi, ωo) * ∫ cosθ dω
     * 0,8 = ρ(ωi, ωo) * π
     * ρ(ωi, ωo) = 0,8 / π
     * <p>
     * For diffuse surfaces where p const, you can write:
     * Ls(ω) = (Li * ρ(ωi, ωo)/π) * π
     * Ls(ω) = Li * ρ(ωi, ωo)
     */
    @Override
    public Radiance sampleBrdf(Hitpoint hitpoint, Ray incomingRay) {
        // The inverse sample weight of the sampled ray would be π which eliminates the division of rhoD by π.
        final Ray cosineDistributedHemisphereSample = getCosineDistributedHemisphereSample(hitpoint);
        // prevent reintersection for the same surface
        cosineDistributedHemisphereSample.mailbox(hitpoint.getHitGeometry());
        return new Radiance(hitpoint.getHitGeometry().getMaterial().getColor(), cosineDistributedHemisphereSample);
    }

    @Override
    public Radiance sampleDirectRadiance(Hitpoint hitpoint, Ray incomingRay) {
        ImmutableVector3 point = hitpoint.getPoint();
        Hitpoint lightSourceSample = scene.getLightSourceSample();
        ImmutableVector3 directionToLightSource = point.minus(lightSourceSample.getPoint());
        float distToLight = directionToLightSource.length();
        hitpoint.setDistance(distToLight);
        directionToLightSource = directionToLightSource.div(distToLight); // manual normalize
        Ray shadowRay = new Ray(lightSourceSample.getPoint(), directionToLightSource.normalize(), Constants.FLT_EPSILON, distToLight - Constants.FLT_EPSILON, false);

        float cosThetaContribHit = directionToLightSource.dot(lightSourceSample.getHitpointNormal());
        float cosThetaSceneHit = directionToLightSource.mult(-1.0f).dot(hitpoint.getHitpointNormal());

        //lightSource hit from correct side?
        if (cosThetaSceneHit < 0.0f || cosThetaContribHit < 0.0f) {
            shadowRay.inverseSampleWeight(0);
            return new Radiance(COLORS.BLACK, shadowRay);
        } else {
            if (scene.isObstructed(shadowRay)) {
                shadowRay.inverseSampleWeight(0);
                return new Radiance(COLORS.BLACK, shadowRay);
            } else {
                shadowRay.inverseSampleWeight(lightSourceSample.getInverseSampleWeight());
                float geomTerm = (cosThetaSceneHit * cosThetaContribHit) / (distToLight * distToLight);
                ImmutableVector3 rhoDiffuse = hitpoint.getHitGeometry().getMaterial().getColor();
                float precisionBound = 10.0f / (rhoDiffuse.getMaxValue());      // bound can include brdf which can soften the geometric term
                ImmutableVector3 result = rhoDiffuse.div(Constants.PI_f).mult(FastMath.min(precisionBound, geomTerm));
                return new Radiance(result.mult(lightSourceSample.getHitGeometry().getMaterial().getEmittance()), shadowRay);
            }
        }
    }


    /**
     * Produces a cosine distributed ray sampled over the hemisphere around hitpoint.
     * @param hitpoint
     * @return new sampled AND yet UNWEIGHTED ray.
     */
    private static Ray getCosineDistributedHemisphereSample(Hitpoint hitpoint) {
        float u = ChromaThreadContext.randomFloatClosedOpen();
        float v = ChromaThreadContext.randomFloatClosedOpen();
        float sqrtU = (float) FastMath.sqrt(u);
        float v2pi = v * Constants.TWO_PI_f;

        float sampleX = (float) FastMath.cos(v2pi) * sqrtU;
        float sampleY = (float) FastMath.sin(v2pi) * sqrtU;
        float sampleZ = (float) FastMath.sqrt(1.0f - u);
        CoordinateSystem coordinateSystem = hitpoint.getCoordinateSystem();

        Vector3 newDirection = new MutableVector3(
                      coordinateSystem.getT1()).mult(sampleX)
                .plus(coordinateSystem.getT2().mult(sampleY))
                .plus(coordinateSystem.getN().mult(sampleZ)).normalize();

        return new Ray(hitpoint.getPoint(), new ImmutableVector3(newDirection));
    }

    @Override
    public void setScene(ChromaScene scene) {
        this.scene = scene;
    }
}
