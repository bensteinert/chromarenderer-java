package net.chromarenderer.math.shader;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.RecursiveRenderer;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author steinerb
 */
public class ShaderEngine {

    private static GeometryScene scene;


    public static Radiance getDirectRadianceSample(Ray incomingRay, Hitpoint hitpoint) {


        Material material = hitpoint.getHitGeometry().getMaterial();

        switch (material.getType()) {
            case DIFFUSE: {
                return DiffuseShader.getDirectRadianceSample(hitpoint);
            }

            case MIRROR: {
                // As long as we use point lights, there is no chance to hit it by mirroring...
                return Radiance.NO_CONTRIBUTION;

            }

            default:
                return Radiance.NO_CONTRIBUTION;
        }
    }


    public static Ray getRecursiveRaySample(Ray incomingRay, Hitpoint hitpoint) {

        Material material = hitpoint.getHitGeometry().getMaterial();

        switch  (material.getType()) {
            case DIFFUSE:{
                return DiffuseShader.getRecursiceRaySample(incomingRay, hitpoint);

            }
            case MIRROR: {
                Vector3 mirrorDirection = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
                return new Ray(hitpoint.getPoint(), new ImmutableVector3(mirrorDirection));
            }

            default:
                return null;

        }
    }


    public static Vector3 brdf(Hitpoint hitpoint, Radiance directRadianceSample, Radiance indirectRadianceSample) {
        return hitpoint.getHitGeometry().getMaterial().getColor().mult(indirectRadianceSample.getColor().plus(directRadianceSample.getColor()));
    }


    public static void setScene(GeometryScene scene) {
        DiffuseShader.scene = scene;
        ShaderEngine.scene = scene;
    }


    public static Radiance getIndirectRadianceSample(Ray incomingRay, Hitpoint hitpoint, RecursiveRenderer simplePathTracer, int depth) {
        Ray recursiveRaySample = getRecursiveRaySample(incomingRay, hitpoint);
        return simplePathTracer.recursiveKernel(recursiveRaySample, depth + 1);
    }
}
