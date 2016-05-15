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
        int depth = 0;

        if (settings.isDirectLightEstimationEnabled()) {
            ptdlKernel(incomingRay, depth, pixel);
        }
        else {
            ptKernel(incomingRay, depth, pixel);
        }
    }


    private void ptKernel(Ray incomingRay, int depth, MutableVector3 result) {
        MutableVector3 pathWeight = new MutableVector3(1.f, 1.f, 1.f);
        Hitpoint hitpoint;
        // L = Le + ∫ fr * Li
        while (pathWeight.getMaxValue() > Constants.FLT_EPSILON && depth < settings.getMaxRayDepth()) {
            // scene intersection
            hitpoint = scene.intersect(incomingRay);
            depth++;

            if (hitpoint.hit()) {
                // Add Le
                Material emitting = hitpoint.getHitGeometry().getMaterial();
                if (MaterialType.EMITTING.equals(emitting.getType())) {
                    result.plus(emitting.getEmittance().mult(pathWeight));
                }

                Radiance fr = ShaderEngine.brdf(hitpoint, incomingRay);
                pathWeight = pathWeight.mult(russianRoulette()).mult(fr.getColor());
                incomingRay = fr.getLightRay();
            }
        }
    }


    private void ptdlKernel(Ray incomingRay, int depth, MutableVector3 result) {
        MutableVector3 pathWeight = new MutableVector3(1.f, 1.f, 1.f);
        Hitpoint hitpoint;
        Radiance fr;
        hitpoint = scene.intersect(incomingRay);
        if (hitpoint.hit()) {

            Material material = hitpoint.getHitGeometry().getMaterial();
            // Add Le - returns 0 if not emitting
            result.plus(material.getEmittance());

            Radiance frDL = ShaderEngine.getDirectRadiance(incomingRay, hitpoint);
            if (frDL.getColor().getMaxValue() > Constants.FLT_EPSILON) {
                result.plus(frDL.getColor());
            }

            fr = ShaderEngine.brdf(hitpoint, incomingRay);
            pathWeight = pathWeight.mult(fr.getColor());
            incomingRay = fr.getLightRay();

            while (pathWeight.getMaxValue() > Constants.FLT_EPSILON && depth < settings.getMaxRayDepth()) {
                hitpoint = scene.intersect(incomingRay);
                depth++;
                if (hitpoint.hit()) {
                    frDL = ShaderEngine.getDirectRadiance(incomingRay, hitpoint);
                    if (frDL.getColor().getMaxValue() > Constants.FLT_EPSILON) {
                        result.plus(frDL.getColor().mult(pathWeight));
                    }

                    fr = ShaderEngine.brdf(hitpoint, incomingRay);
                    pathWeight = pathWeight.mult(russianRoulette()).mult(fr.getColor());
                    incomingRay = fr.getLightRay();
                }
                else break;
            }
        }
    }


    private float russianRoulette() {
        return ChromaThreadContext.randomFloatClosedOpen() > Constants.RR_LIMIT ? 0.f : 1.0f/Constants.RR_LIMIT;
//        return 1.0f;
    }

}
