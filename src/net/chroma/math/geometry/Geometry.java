package net.chroma.math.geometry;

import net.chroma.math.raytracing.Ray;

/**
 * @author steinerb
 */
public interface Geometry {
    float intersect(Ray ray);
}
