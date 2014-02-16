package net.chroma.renderer.cores;

import net.chroma.math.COLORS;
import net.chroma.math.Constants;
import net.chroma.math.ImmutableVector3;
import net.chroma.math.MutableVector3;
import net.chroma.math.Vector3;
import net.chroma.math.geometry.Geometry;
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
    ImmutableVector3 pointLight = new ImmutableVector3(0.0f, 8.0f, 0.0f);
    private boolean completed;
    Camera camera;

    public SimpleRayTracer(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
        createSomeTriangles();
        camera = new PinholeCamera(new ImmutableVector3(0.0f, 0.0f, -50.0f), 20f, 0.01f, 0.01f, imageWidth, imageHeight);
        completed = false;
    }

    private void createSomeTriangles() {
        scene = new ArrayList<>();
//        scene.add(new Triangle(
//                new ImmutableVector3(-1.f, 3.f, 0.f),    //x
//                new ImmutableVector3(.0f, 3.f, -1.f),    //y
//                new ImmutableVector3(1.f, 3.f, 0.f),    //z
//                new ImmutableVector3(0.f, 1.f, 0.f))); //n
//        scene.add(new Triangle(
//                new ImmutableVector3(-2.f, 0.f, 1.f),    //x
//                new ImmutableVector3(.0f, 0.f, -2.f),    //y
//                new ImmutableVector3(2.f, 0.f, 1.f),    //z
//                new ImmutableVector3(0.f, 1.f, 0.f))); //n
        scene.add(new Sphere(new ImmutableVector3(-6.0f, 2.0f, -1.0f), 3.0));
        scene.add(new Sphere(new ImmutableVector3(3.0f, 0.0f, -4.0f), 1.0));
        scene.add(new Sphere(new ImmutableVector3(-1.0f, -3.0f, 2.0f), 4.0));
        scene.add(new Sphere(new ImmutableVector3(7.0f, -4.0f, 1.0f), 2.0));
        //scene.add(new Sphere(new ImmutableVector3(0.0f, 12.0f, 1.0f), 1.0));

        //scene.addAll(SceneFactory.cornellBox(new ImmutableVector3(0, 5, 0), 5));
    }


    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        if(!completed){
            for (int j = 0; j < height; j+=1) {
                for (int i = 0; i < width; i+=1) {
                    Ray ray = camera.getRay(i, j);

                    float hitDistance = Float.MAX_VALUE;
                    Geometry hitGeometry = null;

                    for (Geometry geometry : scene) {
                        float distance = geometry.intersect(ray);
                        if (distance > 0.0f && distance < hitDistance) {
                            hitGeometry = geometry;
                            hitDistance = distance;
                        }
                    }

                    ImmutableVector3 hitpoint = ray.onRay((float) (hitDistance - Constants.FLT_EPSILON));
                    Vector3 color;

                    if(hitGeometry != null){
                        color = COLORS.BLUE;
                        Ray shadowRay = new Ray(hitpoint, pointLight.subtract(hitpoint).normalize());
                        boolean shadowed = false;
                        for (Geometry geometry : scene) {
                            float distance = geometry.intersect(shadowRay);
                            if (distance > 0.0f) {
                                shadowed = true;
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
        return toByteImage();
    }

    @Override
    public boolean isContinuous() {
        return false;
    }

}
