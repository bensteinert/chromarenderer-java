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

        switch (hitpoint.getHitGeometry().getMaterial().getType()) {
            case DIFFUSE:
                return DiffuseShader.sampleDirectRadiance(hitpoint);

            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                return DiffuseShader.sampleDirectRadiance(hitpoint);
                //return Radiance.NO_CONTRIBUTION;

            case MIRROR:
                return MirrorShader.sampleDirectRadiance(incomingRay, hitpoint);

            case PLASTIC:
                return BlinnPhongShader.sampleDirectRadiance(hitpoint, incomingRay);

            default:
                throw new RuntimeException("Unknown MaterialType");
        }
    }


    public static Radiance sampleBrdf(Hitpoint hitpoint, Ray ray) {
        switch (hitpoint.getHitGeometry().getMaterial().getType()) {
            case EMITTING:
                // temporary handling of emitting materials in order to let them participate in reflections
                //return Radiance.NO_CONTRIBUTION;
            case DIFFUSE:
                return DiffuseShader.sampleBrdf(hitpoint, ray);
            case MIRROR:
                return MirrorShader.sampleBrdf(hitpoint, ray);
            case PLASTIC:
                return BlinnPhongShader.sampleBrdf(hitpoint, ray);
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
