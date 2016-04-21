package net.chromarenderer.math.geometry;

import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.math.shader.Material;

/**
 * @author steinerb
 */
public interface Geometry {

    float intersect(Ray ray);

    Geometry transpose(Vector3 transpose);

    Geometry rotate(ImmutableMatrix3x3 rotationY);

    ImmutableVector3 getNormal(ImmutableVector3 hitpoint);

    boolean isPlane();

    Material getMaterial();

    float getArea();

    ImmutableVector3 getUnifDistrSample();

    ImmutableVector3 getSpatialMinimum();

    ImmutableVector3 getSpatialMaximum();

}
