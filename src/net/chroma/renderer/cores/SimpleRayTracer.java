package net.chroma.renderer.cores;

import net.chroma.Renderer;
import net.chroma.math.COLORS;
import net.chroma.math.ImmutableVector3;
import net.chroma.math.Vector3;
import net.chroma.math.geometry.Geometry;
import net.chroma.math.geometry.Triangle;
import net.chroma.math.raytracing.Ray;
import utils.ChromaCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * @author steinerb
 */
public class SimpleRayTracer extends ChromaCanvas implements Renderer {

    private List<Geometry> scene;
    private boolean completed;
    private Vector3 eyePosition;

    public SimpleRayTracer(int imageHeight, int imageWidth) {
        super(imageWidth, imageHeight);
        createSomeTriangles();
        completed = false;
    }

    private void createSomeTriangles() {
        scene = new ArrayList<>();
        scene.add(new Triangle(
                new ImmutableVector3(0.f, 0.f, 1.f),    //x
                new ImmutableVector3(.0f, 1.f, 1.f),    //y
                new ImmutableVector3(1.f, 0.f, 1.f),    //z
                new ImmutableVector3(0.f, 0.f, -1.f))); //n
    }


    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        if(!completed){
            for (int j = 0; j < height; j+=1) {
                for (int i = 0; i < width; i+=1) {

                    Ray ray = primaryRay(i, j);

                    //Vector3 hitpoint;
                    //Vector3 hitpointNormal;
                    float hitDistance = Float.MAX_VALUE;
                    Geometry hitGeometry = null;

                    for (Geometry geometry : scene) {
                        float distance = geometry.intersect(ray);
                        if (distance< hitDistance) {
                            hitGeometry = geometry;
                            hitDistance = distance;
                        }
                    }

                    Vector3 color;
                    if(hitGeometry != null){
                        color = COLORS.BLUE;
                    } else {
                        color = COLORS.BLACK;
                    }

                    pixels[width * j + i] = color;

//                if (object != NULL) {
//                //compute illumination
//                    Ray shadowRay;
//                    shadowRay.direction = lightPosition - pHit;
//                    bool isShadow = false;
//                    for (int k = 0; k < objects.size(); ++k) {
//                        if (Intersect(objects[k], shadowRay)) {
//                            isInShadow = true; break;
//                        }
//                    }
//                }
//                if (!isInShadow)
//                    pixels[i][j] = object->color * light.brightness; else pixels[i][j] = 0;
                }
            }
            completed = true;
        }
        return toByteImage();
    }


    private Ray primaryRay(int i, int j) {
        //TODO
        return null;
    }

}
