package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Constants;
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
public class DielectricShader implements ChromaShader {

    private ChromaScene scene;


    @Override
    public Radiance sampleBrdf(Hitpoint hitpoint, Ray incomingRay) {
        // Hitpoint normal is flipped during intersection to ray side!

        ImmutableVector3 backwardsDirection = incomingRay.getBackwardsDirection();

        Material matFrom;
        Material matTo;
        boolean insideGlass = false;

        ImmutableVector3 n = hitpoint.getHitpointNormal();
        float cosTheta = n.dot(backwardsDirection);

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
            cosTheta = -cosTheta;
            n = n.mult(-1);
            insideGlass = true;
        }

        float etaFrom = matFrom.getIndexOfRefraction(); // TODO: add dispersion (color-dependent IOR here...)
        float etaTo = matTo.getIndexOfRefraction();     // TODO: add dispersion (color-dependent IOR here...)
        final ImmutableVector3 inDir = incomingRay.getDirection();
        final float inDirDotN = n.dot(inDir);

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
                ImmutableVector3 outDirection = ((inDir.minus(n.mult(inDirDotN))).mult(etaFrom)).div(etaTo).minus(n.mult((float) FastMath.sqrt(powCosPhi))).normalize();
                final ImmutableVector3 attenuation = getAttenuation(matFrom, hitpoint.getDistance());
                return new Radiance(attenuation, new Ray(hitpoint.getPoint(), outDirection, Constants.FLT_EPSILON*10.0f, Float.MAX_VALUE));
            }
            else {
//                //reflection case:
                ImmutableVector3 outDirection = VectorUtils.mirror(backwardsDirection, n);
                final ImmutableVector3 attenuation = getAttenuation(matFrom, hitpoint.getDistance());
                final Ray lightRay = new Ray(hitpoint.getPoint(), outDirection);
                if (!insideGlass) {
                    lightRay.mailbox(hitpoint.getHitGeometry());
                }
                return new Radiance(attenuation, lightRay);
            }
//            }
        }
        else {
//            // total internal reflection:
            ImmutableVector3 outDirection = VectorUtils.mirror(backwardsDirection, n);
            final ImmutableVector3 attenuation = getAttenuation(matFrom, hitpoint.getDistance());
            final Ray lightRay = new Ray(hitpoint.getPoint(), outDirection);
            return new Radiance(attenuation, lightRay);
        }
    }


    private ImmutableVector3 getAttenuation(Material matFrom, float distance) {
        final ImmutableVector3 transmission = matFrom.getColor();
        double attR = FastMath.exp(-(1 - transmission.getX()) * distance);    // pathWeight will be attenuted by Beers law
        double attG = FastMath.exp(-(1 - transmission.getY()) * distance);
        double attB = FastMath.exp(-(1 - transmission.getZ()) * distance);
        return new ImmutableVector3(attR, attG, attB);
    }


    @Override
    public Radiance sampleDirectRadiance(Hitpoint hitpoint, Ray incomingRay) {
        Radiance sampledIrradiance = sampleBrdf(hitpoint, incomingRay);
        if (sampledIrradiance.getContribution().getMaxValue()> Constants.FLT_EPSILON) {
            Hitpoint potentialLightSourceHitpoint = scene.intersect(sampledIrradiance.getLightRay());
            if (potentialLightSourceHitpoint.isOn(MaterialType.EMITTING)) {
                Material emitting = potentialLightSourceHitpoint.getHitGeometry().getMaterial();
                ImmutableVector3 radiantIntensity = emitting.getEmittance().mult(sampledIrradiance.getContribution());
                return new Radiance(radiantIntensity, sampledIrradiance.getLightRay()).addContributionFactor(sampledIrradiance.getContributionFactor());
            }
            sampledIrradiance.getLightRay().inverseSampleWeight(0.0f);
        }
        return new Radiance(COLORS.BLACK, sampledIrradiance.getLightRay());
    }


    @Override
    public void setScene(ChromaScene scene) {
        this.scene = scene;
    }
}
