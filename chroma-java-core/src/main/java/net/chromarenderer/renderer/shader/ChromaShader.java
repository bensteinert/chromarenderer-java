package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author bensteinert
 */
interface ChromaShader {

    Radiance sampleBrdf(Hitpoint hitpoint, Ray incomingRay);

    Radiance sampleDirectRadiance(Hitpoint hitpoint, Ray incomingRay);

    void setScene(ChromaScene scene);
}
