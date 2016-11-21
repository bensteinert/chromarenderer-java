package net.chromarenderer.math.geometry;

import net.chromarenderer.math.ChromaFloat;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author bensteinert
 */

//TODO: Check and complete cases ..
public class DomeTest {

    @Test
    public void intersectHit() throws Exception {
        Dome dome = new Dome(Vector3.ONE, new ImmutableVector3(0.5,0.5,0.5).normalize(),2.0f, 1.0f);
        float t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.4, 0.6, 0.3).normalize()));
        Assert.assertTrue(t > 0.0f);
    }

    @Test
    public void intersectMissWithoutPlaneHit() throws Exception {
        //Assert.assertFalse(t > 0.0f);
    }

    @Test
    public void intersectFromBehind() throws Exception {
        //Assert.assertTrue(t > 0.0f);
    }

    @Test
    public void getUnifDistrSample100000Times() throws Exception {
        Dome dome = new Dome(Vector3.ONE, new ImmutableVector3(0.5,0.5,0.5).normalize(),2.0f, 1.0f);
        for (int i = 0; i<100000; i++) {
            ImmutableVector3 sample = dome.getUnifDistrSample();
            Assert.assertTrue(ChromaFloat.approxEqual(sample.minus(dome.getCenter()).length(), dome.getBaseRadius()));
        }
    }



}