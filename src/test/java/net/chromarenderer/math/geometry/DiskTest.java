package net.chromarenderer.math.geometry;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author bensteinert
 */
public class DiskTest {

    @Test
    public void intersectHit() throws Exception {
        Disk disk = new Disk(Vector3.ORIGIN, 2.0f, Vector3.Y_AXIS);
        float t = disk.intersect(new Ray(new ImmutableVector3(-2.0f, 1.0f, 0.0f), new ImmutableVector3(1.0f, -1.0f, 0.0f).normalize()));
        Assert.assertTrue(t > 0.0f);
    }

    @Test
    public void intersectMissWithPlaneHit() throws Exception {
        Disk disk = new Disk(Vector3.ORIGIN, 3.0f, Vector3.Y_AXIS);
        float t = disk.intersect(new Ray(new ImmutableVector3(-1.0f, 1.0f, 0.0f), new ImmutableVector3(-4.0f, -1.0f, 0.0f).normalize()));
        Assert.assertFalse(t > 0.0f);
    }

    @Test
    public void intersectMissWithoutPlaneHit() throws Exception {
        Disk disk = new Disk(Vector3.ORIGIN, 1.0f, Vector3.Y_AXIS);
        float t = disk.intersect(new Ray(new ImmutableVector3(-1.0f, 1.0f, 0.0f), new ImmutableVector3(1.0f, 1.0f, 1.0f).normalize()));
        Assert.assertFalse(t > 0.0f);
    }

    @Test
    public void intersectFromBehind() throws Exception {
        Disk disk = new Disk(Vector3.ORIGIN, 1.0f, Vector3.Y_AXIS);
        float t = disk.intersect(new Ray(new ImmutableVector3(-1.0f, -1.0f, 0.0f), new ImmutableVector3(1.0f, 1.0f, 0.0f).normalize()));
        // No Backface Culling for Disks so far ...
        Assert.assertTrue(t > 0.0f);
    }

    @Test
    public void getUnifDistrSample100000Times() throws Exception {
        Disk disk = new Disk(new ImmutableVector3(-3.8f, -1.0f, 2.7f), 1.0f, new ImmutableVector3(0.3f, 0.6f, 0.0f).normalize());
        for (int i = 0; i < 100000; i++) {
            ImmutableVector3 sample = disk.getUnifDistrSample();
            float distToCenter = sample.minus(disk.getCenter()).length();
            Assert.assertTrue(distToCenter < disk.getRadius());
        }
    }
}