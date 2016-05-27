package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author bensteinert
 */
public class ShaderEngine {

    public static Radiance getDirectRadiance(Ray incomingRay, Hitpoint hitpoint) {

        switch (hitpoint.getHitGeometry().getMaterial().getType()) {
            case DIFFUSE:
                return DiffuseShader.sampleDirectRadiance(hitpoint);

            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                //return DiffuseShader.sampleDirectRadiance(hitpoint);
                return Radiance.NO_CONTRIBUTION;

            case MIRROR:
                return MirrorShader.getDirectRadiance(incomingRay, hitpoint);

            case PLASTIC:
                return BlinnPhongShader.sampleDirectRadiance(incomingRay, hitpoint);

            default:
                return Radiance.NO_CONTRIBUTION;
        }
    }


    public static Ray getRecursiveRaySample(Ray incomingRay, Hitpoint hitpoint) {

        switch (hitpoint.getHitGeometry().getMaterial().getType()) {
            case DIFFUSE:
                return DiffuseShader.getRecursiveRaySample(hitpoint);

            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                return DiffuseShader.getRecursiveRaySample(hitpoint);

            case MIRROR:
                return MirrorShader.getRecursiveRaySample(incomingRay, hitpoint);

            case PLASTIC:
                final float diffSpecRoulette = ChromaThreadContext.randomFloatClosedOpen();
                if (diffSpecRoulette > 0.5f) {  // todo add pdf for diff/spec sampling ...
                    return BlinnPhongShader.getRecursiveRaySample(incomingRay, hitpoint);
                } else {
                    return DiffuseShader.getRecursiveRaySample(hitpoint);
                }

            default:
                throw new RuntimeException("Unknown MaterialType");

        }
    }


    public static Radiance brdf(Hitpoint hitpoint, Ray ray) {
        switch (hitpoint.getHitGeometry().getMaterial().getType()) {
            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                return Radiance.NO_CONTRIBUTION;
            case DIFFUSE:
            case MIRROR:
            case PLASTIC:
                return new Radiance(hitpoint.getHitGeometry().getMaterial().getColor(), getRecursiveRaySample(ray, hitpoint));

            default:
                throw new RuntimeException("Unknown MaterialType");

        }
    }


    public static void setScene(ChromaScene scene) {
        DiffuseShader.scene = scene;
        MirrorShader.scene = scene;
        BlinnPhongShader.scene = scene;
    }

}
