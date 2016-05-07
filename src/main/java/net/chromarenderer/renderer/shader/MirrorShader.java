package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.scene.ChromaScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author bensteinert
 */
class MirrorShader {

    static ChromaScene scene;

    static Radiance getDirectRadiance(Ray incomingRay, Hitpoint hitpoint, float pathWeight) {
        Ray directRadianceRay = getRecursiveRaySample(incomingRay, hitpoint);
        Hitpoint lightSourceSample = scene.intersect(directRadianceRay);

        if (lightSourceSample.hit() && lightSourceSample.isOn(MaterialType.EMITTING)) {
            Material emitting = lightSourceSample.getHitGeometry().getMaterial();
            return new Radiance(emitting.getEmittance().mult(pathWeight), directRadianceRay);
        } else {
            return new Radiance(COLORS.BLACK, directRadianceRay);
        }
    }

    static Ray getRecursiveRaySample(Ray incomingRay, Hitpoint hitpoint) {
        ImmutableVector3 mirrorDirection = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
        return new Ray(hitpoint.getPoint(), mirrorDirection);
    }

}
