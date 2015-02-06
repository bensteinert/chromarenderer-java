package net.chroma;

import junit.framework.Assert;
import net.chroma.math.ImmutableVector3;
import net.chroma.math.geometry.Triangle;
import net.chroma.math.raytracing.Ray;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

public class MoellerTrumboreTriangleIntersectionBenchmark {

    private static Triangle TRIANGLE = new Triangle(
            new ImmutableVector3(0.0f, 0.0f, 1.0f),    //x
            new ImmutableVector3(0.0f, 1.0f, 1.0f),    //y
            new ImmutableVector3(1.0f, 0.0f, 1.0f),    //z
            new ImmutableVector3(1.0f, 1.0f, 1.0f));

    private static Ray RAY = new Ray(new ImmutableVector3(0.2f, 0.2f, 0.f), new ImmutableVector3(0.f, 0.f, 1.f));


    @Test
    public void testIntersect() {
        Assert.assertTrue(benchmarkIntersect() > 0.0f);
    }


    @Benchmark
    public float benchmarkIntersect() {
        // return prevents dead code removal!
        return TRIANGLE.intersect(RAY);
    }

}