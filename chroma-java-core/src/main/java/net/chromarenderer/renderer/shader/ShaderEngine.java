package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author bensteinert
 */
public class ShaderEngine {

    private static ChromaShader[] shaders;

    static {
        shaders = new ChromaShader[5];
        final DiffuseShader diffuseShader = new DiffuseShader();
        shaders[MaterialType.DIFFUSE.ordinal()] = diffuseShader;
        shaders[MaterialType.PLASTIC.ordinal()] = new BlinnPhongShader(diffuseShader);
        shaders[MaterialType.MIRROR.ordinal()] = new MirrorShader();
        shaders[MaterialType.GLASS.ordinal()] = new DielectricShader();
        shaders[MaterialType.EMITTING.ordinal()] = new NoContributionShader();
    }

    public static Radiance getDirectRadiance(Hitpoint hitpoint, Ray incomingRay) {
        return shaders[hitpoint.getHitGeometry().getMaterial().getType().ordinal()].sampleDirectRadiance(hitpoint, incomingRay);
    }


    public static Radiance sampleBrdf(Hitpoint hitpoint, Ray ray) {
        return shaders[hitpoint.getHitGeometry().getMaterial().getType().ordinal()].sampleBrdf(hitpoint, ray);
    }


    public static void setScene(ChromaScene scene) {
        for (ChromaShader shader : shaders) {
            shader.setScene(scene);
        }
    }

}
