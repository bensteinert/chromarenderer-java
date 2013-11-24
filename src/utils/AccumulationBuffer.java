package utils;

import net.chroma.math.Vector3;

/**
 * @author steinerb
 */
public interface AccumulationBuffer {
    SingleThreadedAccumulationBuffer accumulate(Vector3[] input);

    byte[] toByteImage();
}
