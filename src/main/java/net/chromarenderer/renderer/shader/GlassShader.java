package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;
import org.apache.commons.math3.util.FastMath;

/**
 * @author bensteinert
 */
public class GlassShader implements ChromaShader {

    private ChromaScene scene;


    @Override
    public Radiance sampleBrdf(Hitpoint hitpoint, Ray incomingRay) {
        // Hitpoint normal is flipped during intersection to ray side!

        ImmutableVector3 backwardsDirection = incomingRay.getBackwardsDirection();

        Material matFrom;
        Material matTo;

        ImmutableVector3 n = hitpoint.getHitpointNormal();
        final float cosTheta = n.dot(backwardsDirection);

        if (cosTheta > 0.0f) {
            // case from outside to glass
            matFrom = Material.FREE_SPACE;
            matTo = hitpoint.getHitGeometry().getMaterial();
        }
        else {
            // case from inside glass to outside
            matTo = Material.FREE_SPACE;
            matFrom = hitpoint.getHitGeometry().getMaterial();
            // flip normal because it points outwards by convention
            n = n.mult(-1);
        }

        float etaFrom = matFrom.getIndexOfRefraction(); // TODO: add dispersion (color-dependent IOR here...)
        float etaTo = matTo.getIndexOfRefraction();     // TODO: add dispersion (color-dependent IOR here...)
        final ImmutableVector3 inDir = incomingRay.getDirection();
        final float inDirDotN = inDir.dot(n);

        /**
         * See Fundamentals of CG (Pete Shirley) - sec. 9.7 Refraction
         */
        float powCosPhi = 1.0f - (etaFrom * etaFrom * (1.0f - (float) FastMath.pow(inDirDotN, 2.0f))) / (etaTo * etaTo);

        // not sure, contrary to FCG p164
        if (powCosPhi > 0.0f) {

            //Schlicks approximation for reflectivity:
            double R0 = FastMath.pow((etaTo - 1) / (etaTo + 1), 2);
            float reflectance = (float) (R0 + (1 - R0) * FastMath.pow(1 - cosTheta, 5));
            float brdfCase = ChromaThreadContext.randomFloatClosedOpen();

            // inversion method to sample case
            if (brdfCase > reflectance) {
                //refaction case:
                ImmutableVector3 outDirection = ((inDir.minus(n.mult(inDirDotN))).mult(etaFrom)).div(etaTo).minus(n.mult((float) FastMath.sqrt(powCosPhi)));
                final ImmutableVector3 attenuation = getAttenuation(matFrom);
                return new Radiance(attenuation, new Ray(hitpoint.getPoint(), outDirection).inverseSampleWeight((1 - reflectance)/reflectance));
            } else {
                //reflection case:
                ImmutableVector3 outDirection = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
                final ImmutableVector3 attenuation = getAttenuation(matFrom);
                return new Radiance(attenuation, new Ray(hitpoint.getPoint(), outDirection));
            }
        }
        else {
            // total internal reflection:
            ImmutableVector3 outDirection = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
            final ImmutableVector3 attenuation = getAttenuation(matFrom);
            return new Radiance(attenuation, new Ray(hitpoint.getPoint(), outDirection));
        }
    }


    private ImmutableVector3 getAttenuation(Material matFrom) {
        final ImmutableVector3 transmission = matFrom.getTransmission();
        double attR = FastMath.exp(1 - transmission.getX());    // pathWeight will be attenuted by Beers law
        double attG = FastMath.exp(1 - transmission.getY());
        double attB = FastMath.exp(1 - transmission.getZ());
        return new ImmutableVector3(attR, attG, attB);
    }


    @Override
    public Radiance sampleDirectRadiance(Hitpoint hitpoint, Ray incomingRay) {
        Radiance result;
        Radiance sampledIrradiance = sampleBrdf(hitpoint, incomingRay);
        Hitpoint potentialLightSourceHitpoint = scene.intersect(sampledIrradiance.getLightRay());
        if (potentialLightSourceHitpoint.isOn(MaterialType.EMITTING)) {
            Material emitting = potentialLightSourceHitpoint.getHitGeometry().getMaterial();
            ImmutableVector3 radiantIntensity = emitting.getEmittance().mult(sampledIrradiance.getContribution());
            result = new Radiance(radiantIntensity, sampledIrradiance.getLightRay()).addContributionFactor(sampledIrradiance.getContributionFactor());
        } else {
            sampledIrradiance.getLightRay().inverseSampleWeight(0.0f);
            result = new Radiance(COLORS.BLACK, sampledIrradiance.getLightRay());
        }

        return result;
    }


    @Override
    public void setScene(ChromaScene scene) {
        this.scene = scene;
    }
}
