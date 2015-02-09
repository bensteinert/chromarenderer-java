package net.chromarenderer.math.geometry;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.raytracing.Ray;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author steinerb
 */
public class TriangleTest {

    @Test
    public void testIntersectHit() throws Exception {
        Triangle triangle = new Triangle(
                new ImmutableVector3(0.f, 0.f, 1.f),    //x
                new ImmutableVector3(.0f, 1.f, 1.f),    //y
                new ImmutableVector3(1.f, 0.f, 1.f),    //z
                new ImmutableVector3(0.f, 0.f, -1.f));   //n

        float dist = triangle.intersect(new Ray(new ImmutableVector3(0.2f, 0.2f, 0.f), new ImmutableVector3(0.f, 0.f, 1.f)));
        Assert.assertTrue(dist > 0);
    }

    @Test
    public void testIntersectMiss() throws Exception {
        Triangle triangle = new Triangle(
                new ImmutableVector3(0.f, 0.f, 1.f),    //x
                new ImmutableVector3(.0f, 1.f, 1.f),    //y
                new ImmutableVector3(1.f, 0.f, 1.f),    //z
                new ImmutableVector3(0.f, 0.f, -1.f));   //n

        float dist = triangle.intersect(new Ray(new ImmutableVector3(1.2f, 0.2f, 0.f), new ImmutableVector3(0.f, 0.f, 1.f)));
        Assert.assertTrue(dist == 0.0f);

        dist = triangle.intersect(new Ray(new ImmutableVector3(-0.2f, 0.2f, 0.f), new ImmutableVector3(0.f, 0.f, 1.f)));
        Assert.assertTrue(dist == 0.0f);

        dist = triangle.intersect(new Ray(new ImmutableVector3(0.2f, 1.2f, 0.f), new ImmutableVector3(0.f, 0.f, 1.f)));
        Assert.assertTrue(dist == 0.0f);

        dist = triangle.intersect(new Ray(new ImmutableVector3(0.2f, -1.2f, 0.f), new ImmutableVector3(0.f, 0.f, 1.f)));
        Assert.assertTrue(dist == 0.0f);
    }

    @Test
    public void testIntersectRayDirectionParallel() throws Exception {
        Triangle triangle = new Triangle(
                new ImmutableVector3(0.f, 0.f, 1.f),    //x
                new ImmutableVector3(.0f, 1.f, 1.f),    //y
                new ImmutableVector3(1.f, 0.f, 1.f),    //z
                new ImmutableVector3(0.f, 0.f, -1.f));   //n

        float dist = triangle.intersect(new Ray(new ImmutableVector3(0.2f, 0.2f, 0.0f), new ImmutableVector3(0.0f, 1.0f, 0.0f)));
        Assert.assertTrue(dist == 0.f);

        float distEps = triangle.intersect(new Ray(new ImmutableVector3(0.2f, 0.2f, 0.0f), new ImmutableVector3(0.0f, 0.99999f, 0.000001f)));
        Assert.assertTrue(distEps == 0.f);


    }
}
