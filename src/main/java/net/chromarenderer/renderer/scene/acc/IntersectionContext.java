package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.raytracing.Ray;

public class IntersectionContext {
    public Geometry hitGeometry;
    public float hitDistance;
    public Ray ray;
}