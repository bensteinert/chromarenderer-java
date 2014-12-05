package net.chroma.renderer.core;

import net.chroma.math.COLORS;
import net.chroma.math.ImmutableVector3;
import net.chroma.math.MutableVector3;
import net.chroma.math.Vector3;
import net.chroma.math.geometry.Geometry;
import net.chroma.math.geometry.Sphere;
import net.chroma.math.raytracing.Hitpoint;
import net.chroma.math.raytracing.Ray;
import net.chroma.renderer.Renderer;
import net.chroma.renderer.camera.Camera;
import net.chroma.renderer.diag.ChromaStatistics;
import net.chroma.renderer.scene.GeometryScene;
import net.chroma.renderer.scene.Light;
import net.chroma.renderer.scene.SceneFactory;
import utils.ChromaCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * @author steinerb
 */
public class SimpleRayTracer extends ChromaCanvas implements Renderer {

    private final GeometryScene scene;
    private boolean completed;
    private final Camera camera;
    private final ChromaStatistics statistics;


    public SimpleRayTracer(int imageWidth, int imageHeight, Camera camera, ChromaStatistics statistics) {
        super(imageWidth, imageHeight);
        this.camera = camera;
        this.statistics = statistics;
        scene = SceneFactory.cornellBox(new ImmutableVector3(0, 0, 0), 2, createSomeSpheres());
        completed = false;
    }


    private List<Geometry> createSomeSpheres() {
        List<Geometry> result = new ArrayList<>();
        result.add(new Sphere(new ImmutableVector3(0.0f, 0.0f, 0.0f), 0.2, COLORS.PURPLE));
        result.add(new Sphere(new ImmutableVector3(-1.0f, 1.0f, -1.0f), 0.2 , COLORS.RED));
        result.add(new Sphere(new ImmutableVector3(1.0f, -1.0f, 1.0f), 0.2 , COLORS.BLUE));
        result.add(new Sphere(new ImmutableVector3(-1.0f, 1.7f, -1.0f), 0.2, COLORS.GREY));
        result.add(new Sphere(new ImmutableVector3(1.0f, -1.7f, -1.0f), 0.2, COLORS.GREEN));
        return result;
    }


    @Override
    public void renderNextImage(int imgWidth, int imgHeight, int widthOffset, int heightOffset) {
        if (!completed) {
            for (int j = heightOffset; j < imgHeight; j += 1) {
                for (int i = widthOffset; i < imgWidth; i += 1) {
                    Ray cameraRay = camera.getRay(i, j);

                    // scene intersection
                    Hitpoint hitpoint = scene.intersect(cameraRay);

                    Vector3 color = COLORS.GREY;
                    // shading
                    if (hitpoint.hit()) {
                        Light light = scene.enlighten(hitpoint);
                        float cosTheta = light.getLightRay().getDirection().dot(hitpoint.getHitpointNormal());
                        color = hitpoint.getHitGeometry().getColor().mult(light.getColor()).mult(cosTheta);
                    }

                    pixels[width * j + i] = new MutableVector3(color);
                }
            }
            completed = !isContinuous();
        }
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
