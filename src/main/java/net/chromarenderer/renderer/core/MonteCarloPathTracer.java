package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;
import net.chromarenderer.renderer.shader.Material;
import net.chromarenderer.renderer.shader.MaterialType;
import net.chromarenderer.renderer.shader.ShaderEngine;

/**
 * @author bensteinert
 */
public class MonteCarloPathTracer extends AccumulativeRenderer  {

    public MonteCarloPathTracer(ChromaSettings settings, ChromaScene scene, Camera camera) {
        super(settings, scene, camera);
    }


    protected void renderPixel(int j, int i) {
        Ray cameraRay = camera.getRay(i, j);
        kernel(cameraRay, pixels[width * j + i]);
    }


    private void kernel(Ray incomingRay, MutableVector3 pixel) {
        pixel.reset();

        if (settings.isDirectLightEstimationEnabled()) {
            ptdlKernel(incomingRay, pixel);
        }
        else {
            ptKernel(incomingRay, pixel);
        }
    }


    private void ptKernel(Ray incomingRay, MutableVector3 result) {
        MutableVector3 pathWeight = new MutableVector3(1.f, 1.f, 1.f);
        int depth = 0;
        Hitpoint hitpoint;
        // L = Le + ∫ fr * Li
        while (pathWeight.getMaxValue() > Constants.FLT_EPSILON && depth <= settings.getMaxRayDepth()) {
            // scene intersection
            hitpoint = scene.intersect(incomingRay);
            depth++;

            if (hitpoint.hit()) {
                // Add Le
                Material emitting = hitpoint.getHitGeometry().getMaterial();
                if (MaterialType.EMITTING.equals(emitting.getType())) {
                    result.plus(emitting.getEmittance().mult(pathWeight));
                }

                Radiance fr = ShaderEngine.sampleBrdf(hitpoint, incomingRay);
                pathWeight = pathWeight.mult(russianRoulette()).mult(fr.getContribution());
                incomingRay = fr.getLightRay().mailbox(incomingRay.getLastHitGeomerty());
            }
        }
    }


    private void ptdlKernel(Ray incomingRay, MutableVector3 result) {
        MutableVector3 pathWeight = new MutableVector3(1.f, 1.f, 1.f);
        int depth = 1;
        Hitpoint hitpoint;
        Radiance fr;
        hitpoint = scene.intersect(incomingRay);
        if (hitpoint.hit()) {

            Material material = hitpoint.getHitGeometry().getMaterial();

            // Add Le - getEmittance() returns 0 if not emitting
            result.plus(material.getEmittance());

            Radiance irradiance = ShaderEngine.getDirectRadiance(hitpoint, incomingRay);
            result.plus(irradiance.getContribution());

            fr = ShaderEngine.sampleBrdf(hitpoint, incomingRay);
            pathWeight = pathWeight.mult(fr.getContribution());
            incomingRay = fr.getLightRay();

            while (pathWeight.getMaxValue() > Constants.FLT_EPSILON && depth < settings.getMaxRayDepth()) {
                hitpoint = scene.intersect(incomingRay);
                depth++;
                if (hitpoint.hit()) {
                    irradiance = ShaderEngine.getDirectRadiance(hitpoint, incomingRay);
                    result.plus(irradiance.getContribution().mult(pathWeight));

                    fr = ShaderEngine.sampleBrdf(hitpoint, incomingRay);
                    pathWeight = pathWeight.mult(russianRoulette()).mult(fr.getContribution());
                    incomingRay = fr.getLightRay();
                }
                else break;
            }
        }
    }


    private float russianRoulette() {
        return ChromaThreadContext.randomFloatClosedOpen() > Constants.RR_LIMIT ? 0.f : 1.0f/Constants.RR_LIMIT;
    }

}
