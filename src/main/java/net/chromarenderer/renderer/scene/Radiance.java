package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;

/**
 * @author bensteinert
 */
public class Radiance {

    public static final Radiance NO_CONTRIBUTION = new Radiance(COLORS.BLACK, null);

    private final Vector3 radiantIntensity;
    private final Ray lightRay;
    private float contributionFactor = 1.0f;


    public Radiance(Vector3 radiantIntensity, Ray lightRay) {
        this.radiantIntensity = radiantIntensity;
        this.lightRay = lightRay;
    }


    public Vector3 getIntensity() {
        return radiantIntensity;
    }


    public Vector3 getContribution() {
        return radiantIntensity.mult(lightRay.getInverseSampleWeight()/contributionFactor);
    }


    public Ray getLightRay() {
        return lightRay;
    }


    public Radiance addContributionFactor(float factor) {
        contributionFactor *= factor;
        return this;
    }


    public float getContributionFactor() {
        return contributionFactor;
    }
}
