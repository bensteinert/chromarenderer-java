package net.chroma.math.geometry;

import net.chroma.math.ImmutableArrayMatrix3x3;
import net.chroma.math.Vector3;
import net.chroma.math.raytracing.Ray;

/**
 * @author steinerb
 */
public interface Geometry {

    float intersect(Ray ray);

    Geometry transpose(Vector3 transpose);

    Geometry rotate(ImmutableArrayMatrix3x3 rotationY);
}
