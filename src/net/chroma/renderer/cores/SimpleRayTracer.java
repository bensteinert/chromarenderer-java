package net.chroma.renderer.cores;

import net.chroma.Renderer;
import net.chroma.math.Vector3;
import net.chroma.math.geometry.Geometry;
import net.chroma.math.raytracing.Ray;

/**
 * @author steinerb
 */
public class SimpleRayTracer implements Renderer {

    private final int imageHeight;
    private final int imageWidth;

    private Geometry[] scene;

    public SimpleRayTracer(int imageHeight, int imageWidth) {
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
    }


    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        for (int j = 0; j < imageHeight; ++j) {
            for (int i = 0; i < imageWidth; ++i) {

                Ray ray = primaryRay(i, j);

                Vector3 hitpoint;
                Vector3 hitpointNormal;
                float hitDistance = Float.MAX_VALUE;
                Geometry hitGeometry;

//                for (int k = 0; k < objects.size(); ++k) {
//                    if (Intersect(objects[k], primRay, &pHit, &nHit)) {
//                        float distance = Distance(eyePosition, pHit);
//                        if (distance < minDistance) {
//                            object = objects[k];
//                            minDistance = distance;
//                            //update min distance
//                        }
//                    }
//                }
//
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
        return null;
    }

    private Ray primaryRay(int i, int j) {
        return null;
    }

}
