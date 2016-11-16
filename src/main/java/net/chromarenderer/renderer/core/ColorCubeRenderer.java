package net.chromarenderer.renderer.core;

import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.renderer.Renderer;
import net.chromarenderer.renderer.canvas.ChromaCanvas;

/**
 * @author bensteinert
 */
public class ColorCubeRenderer extends ChromaCanvas implements Renderer {


    public ColorCubeRenderer(ChromaSettings settings) {
        super(settings.getImgWidth(), settings.getImgHeight());
        createCubes(pixels, settings.getImgWidth(), settings.getImgHeight());
    }


    @Override
    public void renderNextImage() {
        // TODO: measure pure pixel write effort with continuous mode in a Random Pixel Renderer
    }


    @Override
    public void flush() {

    }


    @Override
    public byte[] get8BitRgbSnapshot() {
        return to8BitImage();
    }


    public static void createCubes(Vector3[] pixels, int imgWidth, int imgHeight) {
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                if (y > imgHeight / 2) {
                    if (x > imgWidth / 2) {
                        pixels[y * imgWidth + x].set(COLORS.PURPLE);
                    } else {
                        pixels[y * imgWidth + x].set(COLORS.BLUE);
                    }
                } else {
                    if (x > imgWidth / 2) {
                        pixels[y * imgWidth + x].set(COLORS.GREEN);
                    } else {
                        pixels[y * imgWidth + x].set(COLORS.RED);
                    }
                }
            }
        }
    }
}
