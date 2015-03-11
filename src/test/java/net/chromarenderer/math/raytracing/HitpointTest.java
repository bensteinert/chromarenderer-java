package net.chromarenderer.math.raytracing;

import net.chromarenderer.math.Vector3;
import org.junit.Test;

public class HitpointTest {

    @Test
    public void testGetUniformHemisphereSample() throws Exception {

        Hitpoint hitpoint = new Hitpoint(null, Vector3.ONE, Vector3.Y_AXIS);
        for (int i = 0; i < 10; i++) {
            System.out.println(hitpoint.getUniformHemisphereSample());
        }
    }
}