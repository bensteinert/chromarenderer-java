package net.chroma.renderer.cores;

import net.chroma.math.COLORS;
import net.chroma.math.Constants;
import net.chroma.math.ImmutableVector3;
import net.chroma.math.MutableVector3;
import net.chroma.math.Vector3;
import net.chroma.math.geometry.Geometry;
import net.chroma.math.geometry.SceneFactory;
import net.chroma.math.geometry.Sphere;
import net.chroma.math.raytracing.Ray;
import net.chroma.renderer.Renderer;
import net.chroma.renderer.cameras.Camera;
import net.chroma.renderer.diag.ChromaStatistics;
import utils.ChromaCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * @author steinerb
 */
public class SimpleRayTracer extends ChromaCanvas implements Renderer {

    private final List<Geometry> scene;
    private final ImmutableVector3 pointLight = new ImmutableVector3(0.0f, 1.5f, 0.0f);
    private boolean completed;
    private final Camera camera;
    private final ChromaStatistics statistics;


    public SimpleRayTracer(int imageWidth, int imageHeight, Camera camera, ChromaStatistics statistics) {
        super(imageWidth, imageHeight);
        this.camera = camera;
        this.statistics = statistics;
        scene = new ArrayList<>();
        createSomeTriangles();
        completed = false;
    }


    private void createSomeTriangles() {

        scene.add(new Sphere(new ImmutableVector3(0.0f, 0.0f, 0.0f), 0.2));
        scene.add(new Sphere(new ImmutableVector3(-1.0f, 1.0f, -1.0f), 0.2));
        scene.add(new Sphere(new ImmutableVector3(1.0f, -1.0f, 1.0f), 0.2));

        scene.add(new Sphere(new ImmutableVector3(-1.0f, 1.7f, -1.0f), 0.2));
        scene.add(new Sphere(new ImmutableVector3(1.0f, -1.7f, -1.0f), 0.2));

        scene.addAll(SceneFactory.cornellBox(new ImmutableVector3(0, 0, 0), 2));
    }


    @Override
    public void renderNextImage(int imgWidth, int imgHeight, int widthOffset, int heightOffset) {
        if (!completed) {
            for (int j = heightOffset; j < imgHeight; j += 1) {
                for (int i = widthOffset; i < imgWidth; i += 1) {
                    Ray cameraRay = camera.getRay(i, j);

                    float hitDistance = Float.MAX_VALUE;
                    Geometry hitGeometry = null;

                    // scene intersection
                    for (Geometry geometry : scene) {
                        float distance = geometry.intersect(cameraRay);
                        if (cameraRay.isOnRay(distance) && distance < hitDistance) {
                            hitGeometry = geometry;
                            hitDistance = distance;
                        }
                    }

                    // shading
                    Vector3 color = COLORS.GREY;
                    if (hitGeometry != null) {
                        ImmutableVector3 hitpoint = cameraRay.onRay(hitDistance);
                        hitpoint = increaseHitpointPrecision(cameraRay, hitGeometry, hitpoint);

                        Vector3 hitpointNormal = hitGeometry.getNormal(hitpoint);
                        hitpoint = hitpoint.plus(hitpointNormal.mult(Constants.FLT_EPSILON));
                        color = hitpointNormal.abs();

                        // Shadow ray computation
                        ImmutableVector3 direction = pointLight.subtract(hitpoint);
                        float distToLightSource = direction.length();
                        Ray shadowRay = new Ray(hitpoint, direction.normalize(), 0.0f, distToLightSource);

                        for (Geometry shadowGeometry : scene) {
                            float distance = shadowGeometry.intersect(shadowRay);
                            if (shadowRay.isOnRay(distance)) {
                                color = COLORS.DARK_BLUE;
                                break;
                            }
                        }
                    }

                    pixels[width * j + i] = new MutableVector3(color);
                }
            }
            completed = true;
        }
    }

    private ImmutableVector3 increaseHitpointPrecision(Ray cameraRay, Geometry hitGeometry, ImmutableVector3 hitpoint) {
        Ray reverseRay = new Ray(hitpoint, cameraRay.getDirection().mult(-1.0f));
        float reverseDistance = hitGeometry.intersect(reverseRay);
        if (reverseDistance > 0) {
            return reverseRay.onRay(reverseDistance);
        } else {
            statistics.reverseRayMissed();
        }
        return hitpoint;
    }

    @Override
    public boolean isContinuous() {
        return false;
    }

    @Override
    public byte[] get8BitRgbSnapshot() {
        return toByteImage();
    }

}
