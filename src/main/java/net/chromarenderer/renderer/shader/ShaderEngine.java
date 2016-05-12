package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author bensteinert
 */
public class ShaderEngine {

    public static Radiance getDirectRadiance(Ray incomingRay, Hitpoint hitpoint) {

        Material material = hitpoint.getHitGeometry().getMaterial();

        switch (material.getType()) {
            case DIFFUSE:
                return DiffuseShader.sampleDirectRadiance(hitpoint);

            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                return DiffuseShader.sampleDirectRadiance(hitpoint);

            case MIRROR: {
                return MirrorShader.getDirectRadiance(incomingRay, hitpoint);
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


    public static Radiance brdf(Hitpoint hitpoint, Ray ray) {
        return new Radiance(hitpoint.getHitGeometry().getMaterial().getColor(), getRecursiveRaySample(ray, hitpoint));
    }


    public static void setScene(ChromaScene scene) {
        DiffuseShader.scene = scene;
        MirrorShader.scene = scene;
    }

}
