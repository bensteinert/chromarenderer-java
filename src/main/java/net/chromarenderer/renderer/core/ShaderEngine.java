package net.chromarenderer.renderer.core;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.VectorUtils;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.raytracing.CoordinateSystem;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.math.shader.Material;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author steinerb
 */
public class ShaderEngine {

    private static GeometryScene scene;


    public static Radiance getDirectRadianceSample(Ray incomingRay, Hitpoint hitpoint) {

        ImmutableVector3 point = hitpoint.getPoint();
        Material material = hitpoint.getHitGeometry().getMaterial();

        switch (material.getType()) {
            case DIFFUSE: {

                ImmutableVector3 direction = scene.pointLight.minus(point);

                float distToLightSource = direction.length();
                Ray shadowRay = new Ray(point, direction.normalize(), 0.0f, distToLightSource);

                float cosThetaSceneHit = direction.dot(hitpoint.getHitpointNormal());

                //geometry hit from correct side?
                if (cosThetaSceneHit > 0.0f) {
                    for (Geometry shadowGeometry : scene.geometryList) {
                        float distance = shadowGeometry.intersect(shadowRay);
                        if (shadowRay.isOnRay(distance)) {
                            return new Radiance(COLORS.BLACK, shadowRay);
                        }
                    }
                } else {
                    return new Radiance(COLORS.BLACK, shadowRay);
                }

                float geomTerm = (cosThetaSceneHit) / (distToLightSource * distToLightSource);
                Vector3 rhoDiffuse = hitpoint.getHitGeometry().getMaterial().getColor();
                float precisionBound = 10.0f / (rhoDiffuse.getMaxValue());                                                        // bound can include brdf which can soften the geometric term
                Vector3 result = rhoDiffuse.div(Constants.PI_f).mult(Math.min(precisionBound, geomTerm));

                return new Radiance(result.mult(2.0f), shadowRay);
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
                float u = ChromaThreadContext.randomFloat();
                float v = ChromaThreadContext.randomFloat();
                float sqrtU = (float) Math.sqrt(u);
                float v2pi = v * Constants.TWO_PI_f;

                float sampleX = (float) Math.cos(v2pi) * sqrtU;
                float sampleY = (float) Math.sin(v2pi) * sqrtU;
                float sampleZ = (float) Math.sqrt(1.0f - u);
                CoordinateSystem coordinateSystem = hitpoint.getCoordinateSystem();

                Vector3 newDirection = new MutableVector3(coordinateSystem.getT1()).mult(sampleX)
                        .plus(coordinateSystem.getT2().mult(sampleY))
                        .plus(coordinateSystem.getN().mult(sampleZ)).normalize();

                return new Ray(hitpoint.getPoint(), new ImmutableVector3(newDirection));

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
        ShaderEngine.scene = scene;

    }
}
