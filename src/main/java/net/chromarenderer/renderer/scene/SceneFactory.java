package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableArrayMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.geometry.Triangle;
import net.chromarenderer.math.shader.Material;
import net.chromarenderer.math.shader.MaterialType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author steinerb
 */
public class SceneFactory {

    private static final Material WAAL_MATERIAL = new Material(MaterialType.DIFFUSE, COLORS.WALL);


    public static GeometryScene cornellBox(ImmutableVector3 center, float halfDimension, List<Geometry> content) {
        List<Triangle> baseBox = buildBaseBox(center, halfDimension);
        List<Geometry> result = new ArrayList<>(content.size() + baseBox.size() * 16);
        result.addAll(content);
        result.addAll(subdivide(subdivide(baseBox)));
        //result.addAll(baseBox);
        return new GeometryScene(result);
    }


    private static List<Triangle> subdivide(List<Triangle> triangles) {
        List<Triangle> result = new ArrayList<>(triangles.size() * 4);
        triangles.stream().map(Triangle::subdivide).forEach(subdivided -> Collections.addAll(result, subdivided));
        return result;
    }


    private static List<Triangle> buildBaseBox(ImmutableVector3 center, float halfDimension) {

        ArrayList<Triangle> result = new ArrayList<>(10);

        Vector3 shiftX = new ImmutableVector3(halfDimension, 0.0f, 0.0f);
        Vector3 shiftY = new ImmutableVector3(0.0f, halfDimension, 0.0f);
        Vector3 shiftZ = new ImmutableVector3(0.0f, 0.0f, halfDimension);

        Vector3 minusCenter = center.mult(-1f);

        ImmutableArrayMatrix3x3 rotationY90 = new ImmutableArrayMatrix3x3(0, 0, 1,
                0, 1, 0,
                -1, 0, 0);

        ImmutableArrayMatrix3x3 rotationZ90 = new ImmutableArrayMatrix3x3(0, -1, 0,
                1, 0, 0,
                0, 0, 1);

        ImmutableVector3 p0x1 = new ImmutableVector3(center.minus(shiftX).minus(shiftY).plus(shiftZ));
        ImmutableVector3 p1x1 = new ImmutableVector3(center.minus(shiftX).minus(shiftY).minus(shiftZ));
        ImmutableVector3 p2x1 = new ImmutableVector3(center.minus(shiftX).plus(shiftY).minus(shiftZ));
        ImmutableVector3 p3x1 = new ImmutableVector3(center.minus(shiftX).plus(shiftY).plus(shiftZ));

        Triangle t0 = new Triangle(p0x1, p1x1, p2x1, WAAL_MATERIAL);
        Triangle t1 = new Triangle(p3x1, p0x1, p2x1, WAAL_MATERIAL);

        //left
        result.add(t0);
        result.add(t1);

        //back
        result.add(t0.transpose(minusCenter).rotate(rotationY90).transpose(center));
        result.add(t1.transpose(minusCenter).rotate(rotationY90).transpose(center));

        //ceil
        Triangle t2 = t0.transpose(minusCenter).rotate(rotationZ90).transpose(center);
        Triangle t3 = t1.transpose(minusCenter).rotate(rotationZ90).transpose(center);
        result.add(t2);
        result.add(t3);

        //right
        Triangle t4 = t2.transpose(minusCenter).rotate(rotationZ90).transpose(center);
        Triangle t5 = t3.transpose(minusCenter).rotate(rotationZ90).transpose(center);
        result.add(t4);
        result.add(t5);

        //floor
        result.add(t4.transpose(minusCenter).rotate(rotationZ90).transpose(center));
        result.add(t5.transpose(minusCenter).rotate(rotationZ90).transpose(center));

        return result;
    }
}
