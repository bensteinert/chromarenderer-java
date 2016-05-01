package net.chromarenderer.renderer.shader;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
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
                // As long as we use point lights, there is no chance to hit it by mirroring...
                return Radiance.NO_CONTRIBUTION;

            }

            default:
                return Radiance.NO_CONTRIBUTION;
        }
    }


    private static Ray getRecursiveRaySample(Ray incomingRay, Hitpoint hitpoint) {

        Material material = hitpoint.getHitGeometry().getMaterial();

        switch (material.getType()) {
            case DIFFUSE:
                return DiffuseShader.getRecursiveRaySample(hitpoint);

            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                return DiffuseShader.getRecursiveRaySample(hitpoint);

            case MIRROR:
                ImmutableVector3 mirrorDirection = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
                return new Ray(hitpoint.getPoint(), mirrorDirection);

            default:
                throw new RuntimeException("Unknown MaterialType");

        }
    }


    public static Vector3 brdf(Hitpoint hitpoint, Radiance directRadianceSample, Radiance indirectRadianceSample) {
        return hitpoint.getHitGeometry().getMaterial().getColor().mult(indirectRadianceSample.getColor().plus(directRadianceSample.getColor()));
    }


    public static void setScene(ChromaScene scene) {
        DiffuseShader.scene = scene;
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
}
