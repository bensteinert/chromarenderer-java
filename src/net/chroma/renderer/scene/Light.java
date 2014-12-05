package net.chroma.renderer.scene;

import net.chroma.math.Vector3;
import net.chroma.math.raytracing.Ray;

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
