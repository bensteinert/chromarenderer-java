package net.chromarenderer.math.raytracing;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import org.junit.Test;

public class HitpointTest {

    @Test
    public void testGetUniformHemisphereSample() throws Exception {
        Hitpoint hitpoint = new Hitpoint(null,1.0f , Vector3.ONE, Vector3.Y_AXIS);
        for (int i = 0; i < 10; i++) {
            ImmutableVector3 sample = hitpoint.getUniformHemisphereSample();
            //System.out.println(sample);
        }
    }
}