package net.chromarenderer.math.geometry;

import net.chromarenderer.UnexpectedGeometryException;
import net.chromarenderer.math.ChromaFloat;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.shader.Material;
import net.chromarenderer.testutils.AssertFloat;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author bensteinert
 */

//TODO: Check and complete cases ..
public class DomeTest {

    @Test(expected = UnexpectedGeometryException.class)
    public void checkDomeFactory_ImpossibleDome() throws Exception {
        Dome.create(Vector3.ORIGIN, 1.0f, Vector3.Y_AXIS, 10.0f, Material.NULL);
    }

    @Test
    public void validateGeometryConditions() throws Exception {
        // Full hemisphere dome (cosTheta=0)
        Dome dome = Dome.create(Vector3.ORIGIN, 1.0f, Vector3.Y_AXIS, 1.0f, Material.NULL);

        Assert.assertTrue(ChromaFloat.approxEqual(dome.getDomeBaseRadius(), 1.0f));
        Assert.assertTrue(ChromaFloat.approxEqual(dome.getCosSemiAngle(), 0.0f));

        float t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.4, 0.6, 0.3).normalize()));
        Assert.assertTrue(t > 0.0f);
    }

    @Test
    public void intersectHemisphere() throws Exception {
        // Full hemisphere dome (cosTheta=0)
        Dome dome = Dome.create(Vector3.ORIGIN, 1.0f, Vector3.Y_AXIS, 1.0f, Material.NULL);

        float t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.0, 1.0, 0.0).normalize()));
        Assert.assertTrue(t > 0.0f);

        t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.0, 0.9, 0.1).normalize()));
        Assert.assertTrue(t > 0.0f);
        t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.0, 0.7, 0.3).normalize()));
        Assert.assertTrue(t > 0.0f);
        t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.0, 0.5, 0.5).normalize()));
        Assert.assertTrue(t > 0.0f);
        t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.0, 0.3, 0.7).normalize()));
        Assert.assertTrue(t > 0.0f);
        t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.0, 0.1, 0.9).normalize()));
        Assert.assertTrue(t > 0.0f);

        // hitting the hemisphere on the outer edge vertex is not defined (float precision)
        t = dome.intersect(new Ray(Vector3.ORIGIN, new ImmutableVector3(0.0, 0.0, 1.0).normalize()));
        Assert.assertFalse(t > 0.0f);
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
        Dome dome = Dome.create(Vector3.ONE, 20.0f, new ImmutableVector3(0.5,0.5,0.5).normalize(), 5.0f, Material.NULL);
        for (int i = 0; i<100000; i++) {
            ImmutableVector3 sample = dome.getUnifDistrSample();
            final float a = sample.minus(dome.getSphereCenter()).length();
            final float b = dome.getSphereRadius();
            AssertFloat.approxEquals(a, b, 100);
        }
    }



}