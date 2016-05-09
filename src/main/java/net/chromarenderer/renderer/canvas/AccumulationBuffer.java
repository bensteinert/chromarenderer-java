package net.chromarenderer.renderer.canvas;

import net.chromarenderer.math.Vector3;

/**
 * @author steinerb
 */
public interface AccumulationBuffer {

    AccumulationBuffer accumulate(Vector3[] input);

    byte[] to8BitImage();

    void flushBuffer();

    float computeL1();
}
