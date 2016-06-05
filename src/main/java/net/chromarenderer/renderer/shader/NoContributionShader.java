package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author bensteinert
 */
class NoContributionShader implements ChromaShader {


    @Override
    public Radiance sampleBrdf(Hitpoint hitpoint, Ray incomingRay) {
        return Radiance.NO_CONTRIBUTION;
    }


    @Override
    public Radiance sampleDirectRadiance(Hitpoint hitpoint, Ray incomingRay) {
        return Radiance.NO_CONTRIBUTION;
    }


    @Override
    public void setScene(ChromaScene scene) {
    }
}
