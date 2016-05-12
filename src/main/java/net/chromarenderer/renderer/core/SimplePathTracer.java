package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.RecursiveRenderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;
import net.chromarenderer.renderer.shader.Material;
import net.chromarenderer.renderer.shader.ShaderEngine;

/**
 * @author bensteinert
 */
public class SimplePathTracer extends AccumulativeRenderer implements RecursiveRenderer {


    public SimplePathTracer(ChromaSettings settings, ChromaScene scene, Camera camera) {
        super(settings, scene, camera);
    }


    protected void renderPixel(int j, int i) {
        Ray cameraRay = camera.getRay(i, j);
        pixels[width * j + i].set(recursiveKernel(cameraRay, 0).getColor());
    }


    public Radiance recursiveKernel(Ray incomingRay, int depth) {
        // scene intersection
        Hitpoint hitpoint = scene.intersect(incomingRay);

        // shading
        Vector3 color = new MutableVector3();
        if (hitpoint.hit()) {
            Material material = hitpoint.getHitGeometry().getMaterial();
            // make light sources visible
            color.plus(hitpoint.getHitGeometry().getMaterial().getEmittance());

            Radiance directRadianceSample = ShaderEngine.getDirectRadiance(incomingRay, hitpoint);
            if (settings.getMaxRayDepth() > depth) {
                Radiance indirectRadianceSample;
                // terminate the path via russian roulette
                float russianRoulette = ChromaThreadContext.randomFloatClosedOpen();
                if (russianRoulette > Constants.RR_LIMIT) {
                    indirectRadianceSample = Radiance.NO_CONTRIBUTION;
                } else {
                    Ray recursiveRaySample = ShaderEngine.getRecursiveRaySample(incomingRay, hitpoint);
                    indirectRadianceSample = recursiveKernel(recursiveRaySample, depth + 1);
                }
                // crude mix of direct an indirect 'colors'...
                color.plus(material.getColor().mult(indirectRadianceSample.getColor().plus(directRadianceSample.getColor())));
            }
        }

        return new Radiance(color, incomingRay);
    }

}
