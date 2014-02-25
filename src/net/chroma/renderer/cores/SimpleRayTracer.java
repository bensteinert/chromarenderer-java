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
import net.chroma.renderer.cameras.PinholeCamera;
import utils.ChromaCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * @author steinerb
 */
public class SimpleRayTracer extends ChromaCanvas implements Renderer {

    private List<Geometry> scene;
    ImmutableVector3 pointLight = new ImmutableVector3(0.0f, 1.5f, 0.0f);
    private boolean completed;
    Camera camera;

    public SimpleRayTracer(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
        createSomeTriangles();
        camera = new PinholeCamera(new ImmutableVector3(0.0f, 0.0f, 8.0f), 0.1f, 0.0001f, 0.0001f, imageWidth, imageHeight);
        completed = false;
    }

    private void createSomeTriangles() {
        scene = new ArrayList<>();

        scene.add(new Sphere(new ImmutableVector3(0.0f, 0.0f, 0.0f), 0.2));
        scene.add(new Sphere(new ImmutableVector3(-1.0f, 1.0f, -1.0f), 0.2));
        scene.add(new Sphere(new ImmutableVector3(1.0f, -1.0f, 1.0f), 0.2));

        scene.add(new Sphere(new ImmutableVector3(-1.0f, 1.7f, -1.0f), 0.2));
        scene.add(new Sphere(new ImmutableVector3(1.0f, -1.7f, -1.0f), 0.2));



        scene.addAll(SceneFactory.cornellBox(new ImmutableVector3(0, 0, 0), 2));
    }


    @Override
    public void renderNextImage(int imgWidth, int imgHeight) {
        if(!completed){
            for (int j = 0; j < height; j+=1) {
                for (int i = 0; i < width; i+=1) {
                    Ray ray = camera.getRay(i, j);

                    float hitDistance = Float.MAX_VALUE;
                    Geometry hitGeometry = null;

                    for (Geometry geometry : scene) {
                        float distance = geometry.intersect(ray);
                        if (distance > ray.getTMin() && distance < hitDistance) {
                            hitGeometry = geometry;
                            hitDistance = distance;
                        }
                    }

                    Vector3 color;
                    if(hitGeometry != null){
                        ImmutableVector3 hitpoint = ray.onRay((hitDistance));
                        Vector3 hitpointNormal = hitGeometry.getNormal(hitpoint);
                        //hitpoint = hitpoint.plus(hitpointNormal.mult(Constants.FLT_EPSILON));
                        color = hitpointNormal.abs();
                        ImmutableVector3 direction = pointLight.subtract(hitpoint);
                        float distToLightSource = direction.length();
                        Ray shadowRay = new Ray(hitpoint, direction.normalize(), 2*Constants.FLT_EPSILON, distToLightSource);

                        for (Geometry geometry : scene) {
                            //TODO: self intersection test for solids. According to normal or stuff like that
                            float distance = geometry.intersect(shadowRay);
                            if (distance > shadowRay.getTMin() && distance < shadowRay.getTMax()) {
                                color = COLORS.DARK_BLUE;
                                break;
                            }
                        }
                    } else {
                        color = COLORS.GREY;
                    }

                    pixels[width * j + i] = new MutableVector3(color);

                }
            }
            completed = true;
        }
    }

    @Override
    public boolean isContinuous() {
        return false;
    }

    @Override
    public byte[] get8BitRGBSnapshot() {
        return toByteImage();
    }

}
