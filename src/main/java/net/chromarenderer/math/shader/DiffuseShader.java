package net.chromarenderer.math.shader;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Constants;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.raytracing.CoordinateSystem;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.core.ChromaThreadContext;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author steinerb
 */
public class DiffuseShader {

    static GeometryScene scene;


    public static Radiance getDirectRadianceSample(Hitpoint hitpoint, float pathWeight, ChromaSettings settings) {

        if (settings.isLightSourceSamplingEnabled()) {
            ImmutableVector3 point = hitpoint.getPoint();
            Hitpoint lightSourceSample = scene.getLightSourceSample();
            ImmutableVector3 direction = lightSourceSample.getPoint().minus(point);

            float distToLight = direction.length();
            Ray shadowRay = new Ray(point, direction.normalize(), Constants.FLT_EPSILON, distToLight - Constants.FLT_EPSILON);

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

            float geomTerm = (cosThetaSceneHit) / (distToLight * distToLight);
            Vector3 rhoDiffuse = hitpoint.getHitGeometry().getMaterial().getColor();
            float precisionBound = 10.0f / (rhoDiffuse.getMaxValue());                                                        // bound can include brdf which can soften the geometric term
            Vector3 lightSourceEmission = lightSourceSample.getHitGeometry().getMaterial().getColor();
            Vector3 result = lightSourceEmission.mult(rhoDiffuse.div(Constants.PI_f).mult(Math.min(precisionBound, geomTerm)).mult(lightSourceSample.getInverseSampleWeight()));

            return new Radiance(result.mult(2.0f * pathWeight), shadowRay);
        }
        else {
            ImmutableVector3 direction = hitpoint.getUniformHemisphereSample();
            Ray ray = new Ray(hitpoint.getPoint(), direction);
            Hitpoint lightSourceSample = scene.intersect(ray);

            if (lightSourceSample.hit() && lightSourceSample.isOn(MaterialType.EMITTING)) {
                return new Radiance(lightSourceSample.getHitGeometry().getMaterial().getColor().mult(pathWeight), ray);
            } else {
                return new Radiance(COLORS.BLACK, ray);
            }
        }
        /**
         * Ls(ω) = ∫ Li * rho * cosθ dω
         * MC computation with one sample replaces ∫:
         * Ls(ω) = (Li * rho * cosθ) / p(ω) where p(ω) = cos(θ)/π
         *
         * Ls(ω) = Li + rho *
         *
         *
         * We sample over the area of all light sources:
         */
    }


    public static Ray getRecursiveRaySample(Ray incomingRay, Hitpoint hitpoint) {
        float u = ChromaThreadContext.randomFloatClosedOpen();
        float v = ChromaThreadContext.randomFloatClosedOpen();
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
}
