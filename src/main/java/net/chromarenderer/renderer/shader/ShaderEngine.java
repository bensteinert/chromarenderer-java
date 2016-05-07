package net.chromarenderer.renderer.shader;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.RecursiveRenderer;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author steinerb
 */
public class ShaderEngine {


    public static Radiance getDirectRadianceSample(Ray incomingRay, Hitpoint hitpoint, float pathWeight, ChromaSettings settings) {

        Material material = hitpoint.getHitGeometry().getMaterial();

        switch (material.getType()) {
            case DIFFUSE:
                return DiffuseShader.getDirectRadianceSample(hitpoint, pathWeight, settings);

            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                return DiffuseShader.getDirectRadianceSample(hitpoint, pathWeight, settings);

            case MIRROR: {
                return MirrorShader.getDirectRadiance(incomingRay, hitpoint, pathWeight);
            }

            default:
                return Radiance.NO_CONTRIBUTION;
        }
    }


    public static Ray getRecursiveRaySample(Ray incomingRay, Hitpoint hitpoint) {

        Material material = hitpoint.getHitGeometry().getMaterial();

        switch (material.getType()) {
            case DIFFUSE:
                return DiffuseShader.getRecursiveRaySample(hitpoint);

            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                return DiffuseShader.getRecursiveRaySample(hitpoint);

            case MIRROR:
                return MirrorShader.getRecursiveRaySample(incomingRay, hitpoint);

            default:
                throw new RuntimeException("Unknown MaterialType");

        }
    }

    public static Radiance getIndirectRadianceSample(Ray incomingRay, Hitpoint hitpoint, RecursiveRenderer simplePathTracer, int depth, float pathWeight) {
        float russianRoulette = ChromaThreadContext.randomFloatClosedOpen();
        if (russianRoulette > Constants.RR_LIMIT) {
            return Radiance.NO_CONTRIBUTION;
        } else {
            Ray recursiveRaySample = getRecursiveRaySample(incomingRay, hitpoint);
            return simplePathTracer.recursiveKernel(recursiveRaySample, depth + 1, pathWeight / Constants.RR_LIMIT);
        }
    }


    public static ImmutableVector3 brdf(Hitpoint hitpoint, Radiance directRadianceSample, Radiance indirectRadianceSample) {
        return hitpoint.getHitGeometry().getMaterial().getColor().mult(indirectRadianceSample.getColor().plus(directRadianceSample.getColor()));
    }

    public static ImmutableVector3 brdf2(Hitpoint hitpoint, Ray ray) {
        return hitpoint.getHitGeometry().getMaterial().getColor();
    }


    public static void setScene(ChromaScene scene) {
        DiffuseShader.scene = scene;
        MirrorShader.scene = scene;
    }

}
