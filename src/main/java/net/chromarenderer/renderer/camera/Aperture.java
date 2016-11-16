package net.chromarenderer.renderer.camera;

import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.raytracing.Ray;
import net.chromarenderer.renderer.shader.Material;

/**
 * Special sort of geometry that represent a camera aperture.
 *
 * @author bensteinert
 */
public class Aperture implements Geometry {
    @Override
    public float intersect(Ray ray) {
        return 0;
    }

    @Override
    public Geometry transpose(Vector3 transpose) {
        return null;
    }

    @Override
    public Geometry rotate(ImmutableMatrix3x3 rotationY) {
        return null;
    }

    @Override
    public ImmutableVector3 getNormal(ImmutableVector3 hitpoint) {
        return null;
    }

    @Override
    public boolean isPlane() {
        return false;
    }

    @Override
    public Material getMaterial() {
        return null;
    }

    @Override
    public void setMaterial(Material material) {

    }

    @Override
    public float getArea() {
        return 0;
    }

    @Override
    public ImmutableVector3 getUnifDistrSample() {
        return null;
    }

    @Override
    public ImmutableVector3 getSpatialMinimum() {
        return null;
    }

    @Override
    public ImmutableVector3 getSpatialMaximum() {
        return null;
    }
}
