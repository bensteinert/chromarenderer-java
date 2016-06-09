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
class MirrorShader implements ChromaShader {

    private ChromaScene scene;

    @Override
    public Radiance sampleDirectRadiance(Hitpoint hitpoint, Ray incomingRay) {
        Ray directRadianceRay = getRecursiveRaySample(hitpoint, incomingRay);
        Hitpoint lightSourceSample = scene.intersect(directRadianceRay);

        if (lightSourceSample.isOn(MaterialType.EMITTING)) {
            Material emitting = lightSourceSample.getHitGeometry().getMaterial();
            return new Radiance(emitting.getEmittance().mult(1.0f - Constants.FLT_EPSILON), directRadianceRay);
        } else {
            return new Radiance(COLORS.BLACK, directRadianceRay);
        }
    }

    @Override
    public Radiance sampleBrdf(Hitpoint hitpoint, Ray incomingRay) {
        return new Radiance(COLORS.WHITE, getRecursiveRaySample(hitpoint, incomingRay));
    }


    private Ray getRecursiveRaySample(Hitpoint hitpoint, Ray incomingRay) {
        final ImmutableVector3 mirrorDirection = VectorUtils.mirror(incomingRay.getDirection().mult(-1.0f), hitpoint.getHitpointNormal());
        return new Ray(hitpoint.getPoint(), mirrorDirection);
    }

    @Override
    public void setScene(ChromaScene scene) {
        this.scene = scene;
    }

}
