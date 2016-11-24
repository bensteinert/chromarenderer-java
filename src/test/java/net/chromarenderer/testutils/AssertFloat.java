package net.chromarenderer.testutils;

import net.chromarenderer.math.ChromaFloat;
import org.junit.Assert;

/**
 * @author bensteinert
 */
public class AssertFloat {

    public static void approxEquals(float a, float b) {
        approxEquals(a, b, 1);
    }


    public static void approxEquals(float a, float b, int epsilonShift) {
        Assert.assertTrue(String.format("%f and %f are not equal considering a reasonable epsilon.", a, b), ChromaFloat.approxEqual(a, b, epsilonShift));
    }
}
