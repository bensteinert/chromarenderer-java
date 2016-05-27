package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;

/**
 * @author steinerb
 */
public class Radiance {

    public static final Radiance NO_CONTRIBUTION = new Radiance(COLORS.BLACK, null);

    private final Vector3 color;
    private final Ray lightRay;


    public Radiance(Vector3 color, Ray lightRay) {
        this.color = color;
        this.lightRay = lightRay;
    }


    public Vector3 getColor() {
        return color;
    }

    public Vector3 getContribution() {
        return color.mult(getInvSampleWeight());
    }

    public float getInvSampleWeight() {
        return  lightRay != null? lightRay.getSampleWeight() : 0.0f;
    }


    public Ray getLightRay() {
        return lightRay;
    }
}
