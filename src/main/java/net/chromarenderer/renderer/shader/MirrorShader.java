package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Constants;
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


    static Radiance sampleDirectRadiance(Ray incomingRay, Hitpoint hitpoint) {
        Ray directRadianceRay = getRecursiveRaySample(hitpoint, incomingRay);
        Hitpoint lightSourceSample = scene.intersect(directRadianceRay);

        if (lightSourceSample.isOn(MaterialType.EMITTING)) {
            Material emitting = lightSourceSample.getHitGeometry().getMaterial();
            return new Radiance(emitting.getEmittance().mult(1.0f - Constants.FLT_EPSILON), directRadianceRay);
        } else {
            return new Radiance(COLORS.BLACK, directRadianceRay);
        }
    }


    static Radiance sampleBrdf(Hitpoint hitpoint, Ray incomingRay) {
        return new Radiance(COLORS.WHITE, getRecursiveRaySample(hitpoint, incomingRay));
    }


    static Ray getRecursiveRaySample(Hitpoint hitpoint, Ray incomingRay) {
        final ImmutableVector3 mirrorDirection = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
        return new Ray(hitpoint.getPoint(), mirrorDirection);
    }

}
