package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableMatrix3x3;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.geometry.SimpleTriangle;
import net.chromarenderer.math.geometry.Sphere;
import net.chromarenderer.math.geometry.Triangle;
import net.chromarenderer.renderer.camera.Camera;
import net.chromarenderer.renderer.shader.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bensteinert
 */
public class SceneFactory {

    private static final Material WALL_MATERIAL = Material.createDiffuseMaterial(COLORS.WALL);
    private static final Material LEFT_WALL_MATERIAL = Material.createDiffuseMaterial(COLORS.RED);
    private static final Material RIGHT_WALL_MATERIAL = Material.createDiffuseMaterial(COLORS.GREEN);
    private static final Material CORNELL_LIGHT = Material.createEmittingMaterial(COLORS.WHITE, 10.f);

    public static GeometryScene cornellBox(Camera camera, ImmutableVector3 center, float halfDimension, List<Geometry> content) {
        List<Triangle> baseBox = buildBaseBox(center, halfDimension);
        List<Triangle> lightSource = buildLightSource(halfDimension);
        List<Geometry> result = new ArrayList<>(lightSource.size() + content.size() + baseBox.size() * 16);
        result.addAll(subdivide(baseBox));
        result.addAll(content);
        result.addAll(lightSource);
        return new GeometryScene(result, camera);
    }


    private static List<Triangle> buildLightSource(float halfDimension) {
        List<Triangle> result = new ArrayList<>();
        ImmutableVector3 t0p0 = new ImmutableVector3(halfDimension / 4, 1.998999, -halfDimension / 4);
        ImmutableVector3 t0p1 = new ImmutableVector3(-halfDimension / 4, 1.998999, halfDimension / 4);
        ImmutableVector3 t0p2 = new ImmutableVector3(-halfDimension / 4, 1.998999, -halfDimension / 4);
        ImmutableVector3 t1p0 = new ImmutableVector3(halfDimension / 4, 1.998999, -halfDimension / 4);
        ImmutableVector3 t1p1 = new ImmutableVector3(halfDimension / 4, 1.998999, halfDimension / 4);
        ImmutableVector3 t1p2 = new ImmutableVector3(-halfDimension / 4, 1.998999, halfDimension / 4);

        SimpleTriangle l0 = new SimpleTriangle(t0p0, t0p1, t0p2);
        SimpleTriangle l1 = new SimpleTriangle(t1p0, t1p1, t1p2);
        l0.setMaterial(CORNELL_LIGHT);
        l1.setMaterial(CORNELL_LIGHT);
        Collections.addAll(result, l0, l1);
        return result;
    }


    public static List<Geometry> createSomeSpheres() {
        List<Geometry> result = new ArrayList<>();
        result.add(new Sphere(new ImmutableVector3(1.2f, -1.8f, 1.6f), 0.2, Material.createDiffuseMaterial(COLORS.PURPLE)));
        result.add(new Sphere(new ImmutableVector3(-1.0f, -1.7f, 1.6f), 0.3, Material.createDiffuseMaterial(COLORS.YELLOW)));
        result.add(new Sphere(new ImmutableVector3(-0.8, -1.5f, -0.6f), 0.5, Material.createPlasticMaterial(COLORS.ORANGE, 100.0f)));
        result.add(new Sphere(new ImmutableVector3(1.2f, -1.6f, -0.7f), 0.4, Material.MIRROR));
        result.add(new Sphere(new ImmutableVector3(0.2, -1.3999f, 0.4f), 0.6, Material.createGlassMaterial(COLORS.BLUE, 1.5f)));
        return result;
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

        ImmutableMatrix3x3 rotationY90 = new ImmutableMatrix3x3(0, 0, 1,
                0, 1, 0,
                -1, 0, 0);

        ImmutableMatrix3x3 rotationZ90 = new ImmutableMatrix3x3(0, -1, 0,
                1, 0, 0,
                0, 0, 1);

        ImmutableVector3 p0x1 = new ImmutableVector3(center.minus(shiftX).minus(shiftY).plus(shiftZ));
        ImmutableVector3 p1x1 = new ImmutableVector3(center.minus(shiftX).minus(shiftY).minus(shiftZ));
        ImmutableVector3 p2x1 = new ImmutableVector3(center.minus(shiftX).plus(shiftY).minus(shiftZ));
        ImmutableVector3 p3x1 = new ImmutableVector3(center.minus(shiftX).plus(shiftY).plus(shiftZ));

        Triangle t0 = SimpleTriangle.createTriangle(p0x1, p1x1, p2x1, WALL_MATERIAL);
        Triangle t1 = SimpleTriangle.createTriangle(p3x1, p0x1, p2x1, WALL_MATERIAL);
        result.add(t0);
        result.add(t1);

        //back
        result.add(t0.transpose(minusCenter).rotate(rotationY90).transpose(center));
        result.add(t1.transpose(minusCenter).rotate(rotationY90).transpose(center));

        //front
        result.add(t0.transpose(minusCenter).rotate(rotationY90).rotate(rotationY90).rotate(rotationY90).transpose(center));
        result.add(t1.transpose(minusCenter).rotate(rotationY90).rotate(rotationY90).rotate(rotationY90).transpose(center));

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

        // all transformations clone the primitives, hence we can change materials safely
        t0.setMaterial(LEFT_WALL_MATERIAL);
        t1.setMaterial(LEFT_WALL_MATERIAL);
        t4.setMaterial(RIGHT_WALL_MATERIAL);
        t5.setMaterial(RIGHT_WALL_MATERIAL);

        return result;
    }
}
