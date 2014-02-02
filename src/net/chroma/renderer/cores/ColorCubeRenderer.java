package net.chroma.renderer.cores;

import net.chroma.Renderer;
import net.chroma.math.COLORS;
import net.chroma.math.Vector3;
import utils.ChromaCanvas;

/**
 * @author steinerb
 */
public class ColorCubeRenderer extends ChromaCanvas implements Renderer {

    public ColorCubeRenderer(int imgWidth, int imgHeight) {
        super(imgWidth, imgHeight);
        createCubes(pixels, imgWidth, imgHeight);
    }

    @Override
    public byte[] renderNextImage(int imgWidth, int imgHeight) {
        return toByteImage();
    }

    public static void createCubes(Vector3[] pixels, int imgWidth, int imgHeight) {
        for (int y=0; y<imgHeight; y++) {
            for(int x=0; x<imgWidth; x++) {
                if(y > imgHeight/2) {
                    if(x > imgWidth/2) {
                        pixels[y*imgWidth + x].set(COLORS.RED);
                    } else{
                        pixels[y*imgWidth + x].set(COLORS.BLUE);
                    }
                } else {
                    if(x > imgWidth/2) {
                        pixels[y*imgWidth + x].set(COLORS.GREEN);
                    } else{
                        pixels[y*imgWidth + x].set(COLORS.PURPLE);
                    }
                }
            }
        }
    }
}
