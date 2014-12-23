package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;

/**
 * @author steinerb
 */
public class Light {

    private final Vector3 color;
    private final Ray lightRay;


    public Light(Vector3 color, Ray lightRay) {
        this.color = color;
        this.lightRay = lightRay;
    }


    public Vector3 getColor() {
        return color;
    }


    public Ray getLightRay() {
        return lightRay;
    }
}
