package net.chromarenderer.math.geom;

import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.SingleShotTime)
@Fork(value = 1)
@Measurement(batchSize = 1000000, iterations = 100)
public class TriangleBenchmark {

    private static Triangle TRIANGLE = new Triangle(
            new Vector3(0.f, 0.f, 1.f),    //x
            new Vector3(.0f, 1.f, 1.f),    //y
            new Vector3(1.f, 0.f, 1.f),    //z
            new Vector3(0.f, 0.f, -1.f));


    private static Ray RAY = new Ray(new Vector3(0.2f, 0.2f, 0.f), new Vector3(0.f, 0.f, 1.f), 0.0f, Float.MAX_VALUE);


    @Benchmark
    public float benchmarkMoellerTrumboreTriangleIntersect() {
        return TRIANGLE.intersect(RAY);
    }


}
