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
class BlinnPhongShader implements ChromaShader {

    private ChromaScene scene;
    private DiffuseShader diffuseShader;


    BlinnPhongShader(DiffuseShader diffuseShader) {
        this.diffuseShader = diffuseShader;
    }


    @Override
    public Radiance sampleBrdf(Hitpoint hitpoint, Ray incomingRay) {
        final float diffSpecRoulette = ChromaThreadContext.randomFloatClosedOpen();

        Radiance radiance;
        // TODO: might be material dependent in order to adapt according to diffuse color (rhoD=black doesn't need diffuse samples)
        if (diffSpecRoulette < 0.7f) {
            radiance = diffuseShader.sampleBrdf(hitpoint, incomingRay).addContributionFactor(0.7f);
        } else {
            radiance = sampleGlossyPart(hitpoint, incomingRay).addContributionFactor(0.3f);
        }

        return radiance;
    }


    private static Radiance sampleGlossyPart(Hitpoint hitpoint, Ray incomingRay) {
        final float lobeNumber = hitpoint.getHitGeometry().getMaterial().getSpecularityHardness();
        ImmutableVector3 mirrorDir = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
        final ImmutableVector3 newDirection = getCosineDistributedLobeSample(mirrorDir, hitpoint, lobeNumber);
        float cosTheta = newDirection.dot(hitpoint.getHitpointNormal());
        final Ray sampledRay = new Ray(hitpoint.getPoint(), new ImmutableVector3(newDirection));

        if (cosTheta < Constants.FLT_EPSILON) {
            // case: sample below surface ray weight gets 0
            sampledRay.inverseSampleWeight(0.0f);
        } else {
            sampledRay.inverseSampleWeight(((lobeNumber + 2.0f) / (lobeNumber + 1.0f)));// * cosTheta);
            // prevent reintersection for the same surface
            sampledRay.mailbox(hitpoint.getHitGeometry());
        }

        return new Radiance(COLORS.GREY, sampledRay);
    }


    @Override
    public Radiance sampleDirectRadiance(Hitpoint hitpoint, Ray incomingRay) {
        Radiance sampledIrradiance = sampleBrdf(hitpoint, incomingRay);
        Radiance result;

        Hitpoint potentialLightSourceHitpoint = scene.intersect(sampledIrradiance.getLightRay());
        if (potentialLightSourceHitpoint.isOn(MaterialType.EMITTING)) {
            // A glossy plastic layer coating always reflects the full color of the light source!
            // The reflection is not influenced by the material color!
            Material emitting = potentialLightSourceHitpoint.getHitGeometry().getMaterial();
            ImmutableVector3 radiantIntensity = emitting.getEmittance().mult(sampledIrradiance.getContribution());
            result = new Radiance(radiantIntensity, sampledIrradiance.getLightRay()).addContributionFactor(sampledIrradiance.getContributionFactor());
        } else {
            sampledIrradiance.getLightRay().inverseSampleWeight(0.0f);
            result = new Radiance(COLORS.BLACK, sampledIrradiance.getLightRay());
        }

        return result;
    }


    /**
     * Produces a cosine distributed ray sampled within a lobe defined by lobeNumber around mirror direction.
     *
     * @param direction  incoming direction
     * @param hitpoint   hitpoint on the surface to shade
     * @param lobeNumber characteristic
     * @return new sampled ray.
     */
    private static ImmutableVector3 getCosineDistributedLobeSample(ImmutableVector3 direction, Hitpoint hitpoint, float lobeNumber) {

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


    @Override
    public void setScene(ChromaScene scene) {
        this.scene = scene;
    }

}
