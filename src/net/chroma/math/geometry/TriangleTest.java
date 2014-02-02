package net.chroma.math.geometry;

import junit.framework.Assert;
import net.chroma.math.ImmutableVector3;
import net.chroma.math.raytracing.Ray;
import org.junit.Test;

/**
 * @author steinerb
 */
public class TriangleTest {

    @Test
    public void testIntersect() throws Exception {
        Triangle triangle = new Triangle(
                new ImmutableVector3(0.f, 0.f, 1.f),    //x
                new ImmutableVector3(.0f, 1.f, 1.f),    //y
                new ImmutableVector3(1.f, 0.f, 1.f),    //z
                new ImmutableVector3(0.f, 0.f, 1.f));   //n

        float dist = triangle.intersect(new Ray(new ImmutableVector3(0.2f, 0.2f, 0.f), new ImmutableVector3(0.f, 0.f, 1.f)));

        Assert.assertTrue(dist > 0);
    }
}
