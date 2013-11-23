package utils;

import javafx.scene.image.PixelReader;
import net.chroma.math.Vector3;

/**
 * @author steinerb
 */
public class AccumulationBuffer {

    private int width;
    private int height;

    private Vector3[] pixels;
    private int accCount;


    public AccumulationBuffer accumulate(Vector3[] input){
        if(input.length != width * height){
            throw new IllegalArgumentException("Mismatching Accumulation buffer input!");
        }
        // maybe using collection streams?
        for (int i = 0; i < width * height; i++) {
            pixels[i] =  pixels[i].mult(accCount).plus(input[i]).div(accCount+1);
        }
        accCount ++;
    }


}
