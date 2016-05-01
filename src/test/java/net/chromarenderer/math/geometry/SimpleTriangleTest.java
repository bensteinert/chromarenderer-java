package net.chromarenderer.math.geometry;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.raytracing.Ray;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author steinerb
 */
public class SimpleTriangleTest {

    @Test
    public void testIntersectHit() throws Exception {
        SimpleTriangle triangle = new SimpleTriangle(
                new ImmutableVector3(0.f, 0.f, 1.f),    //x
                new ImmutableVector3(.0f, 1.f, 1.f),    //y
                new ImmutableVector3(1.f, 0.f, 1.f),    //z
                new ImmutableVector3(0.f, 0.f, -1.f));   //n

        float dist = triangle.intersect(new Ray(new ImmutableVector3(0.2f, 0.2f, 0.f), new ImmutableVector3(0.f, 0.f, 1.f)));
        Assert.assertTrue(dist > 0);
    }

    @Test
    public void testIntersectMiss() throws Exception {
        SimpleTriangle triangle = new SimpleTriangle(
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
        SimpleTriangle triangle = new SimpleTriangle(
                new ImmutableVector3(0.f, 0.f, 1.f),    //x
                new ImmutableVector3(.0f, 1.f, 1.f),    //y
                new ImmutableVector3(1.f, 0.f, 1.f),    //z
                new ImmutableVector3(0.f, 0.f, -1.f));   //n

        float dist = triangle.intersect(new Ray(new ImmutableVector3(0.2f, 0.2f, 0.0f), new ImmutableVector3(0.0f, 1.0f, 0.0f)));
        Assert.assertTrue(dist == 0.f);

        float distEps = triangle.intersect(new Ray(new ImmutableVector3(0.2f, 0.2f, 0.0f), new ImmutableVector3(0.0f, 0.99999f, 0.000001f)));
        Assert.assertTrue(distEps == 0.f);
    }


    @Test
    public void testSubdivide() throws Exception {
        SimpleTriangle triangle = new SimpleTriangle(
                new ImmutableVector3(0.f, 0.f, 0.f),    //x
                new ImmutableVector3(2.f, 0.f, 0.f),    //y
                new ImmutableVector3(2.f, 2.f, 0.f));   //z;

        SimpleTriangle expectedT1 = new SimpleTriangle(
                new ImmutableVector3(0.f, 0.f, 0.f),    //x
                new ImmutableVector3(1.f, 0.f, 0.f),    //y
                new ImmutableVector3(1.f, 1.f, 0.f));   //z;

        SimpleTriangle expectedT2 = new SimpleTriangle(
                new ImmutableVector3(1.f, 0.f, 0.f),    //x
                new ImmutableVector3(2.f, 0.f, 0.f),    //y
                new ImmutableVector3(2.f, 1.f, 0.f));   //z;

        SimpleTriangle expectedT3 = new SimpleTriangle(
                new ImmutableVector3(2.f, 1.f, 0.f),    //x
                new ImmutableVector3(2.f, 2.f, 0.f),    //y
                new ImmutableVector3(1.f, 1.f, 0.f));   //z;

        SimpleTriangle expectedT4 = new SimpleTriangle(
                new ImmutableVector3(1.f, 0.f, 0.f),    //x
                new ImmutableVector3(2.f, 1.f, 0.f),    //y
                new ImmutableVector3(1.f, 1.f, 0.f));   //z;


        SimpleTriangle[] subdividedTriangles = triangle.subdivide();

        Assert.assertEquals(expectedT1, subdividedTriangles[0]);
        Assert.assertEquals(expectedT2, subdividedTriangles[1]);
        Assert.assertEquals(expectedT3, subdividedTriangles[2]);
        Assert.assertEquals(expectedT4, subdividedTriangles[3]);
    }
}
