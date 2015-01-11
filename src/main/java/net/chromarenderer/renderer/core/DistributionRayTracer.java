package net.chromarenderer.renderer.core;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.MutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Hitpoint;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.canvas.ChromaCanvas;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.Radiance;

/**
 * @author steinerb
 */
public class DistributionRayTracer extends ChromaCanvas implements Renderer {

    private final GeometryScene scene;
    private boolean completed;
    private final Camera camera;


    public DistributionRayTracer(int imageWidth, int imageHeight, GeometryScene scene, Camera camera) {
        super(imageWidth, imageHeight);
        this.scene = scene;
        this.camera = camera;
        completed = false;
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
                        Radiance radiance = scene.getRadianceSample(hitpoint);
                        //float cosTheta = light.getLightRay().getDirection().dot(hitpoint.getHitpointNormal());
                        color = hitpoint.getHitGeometry().getColor().mult(radiance.getColor());
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
        return to8BitImage();
    }

}
