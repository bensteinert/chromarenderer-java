package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;

/**
 * @author bensteinert
 */
public class Material {

    public static final Material NULL = new Material(MaterialType.NULL, COLORS.BLACK, 0, 0, 0);
    public static final Material MIRROR = new Material(MaterialType.MIRROR, COLORS.WHITE, 0, 0, 99999);
    public static final Material FREE_SPACE = new Material(MaterialType.NULL, COLORS.WHITE, 1, 0, 1);

    private final MaterialType type;
    private final ImmutableVector3 color;
    private final float indexOfRefraction;
    private final float power;
    private final float specularityHardness;


    public Material(MaterialType type, ImmutableVector3 color, float indexOfRefraction, float power, float specularityHardness) {
        this.type = type;
        this.color = color;
        this.indexOfRefraction = indexOfRefraction;
        this.power = power;
        this.specularityHardness = specularityHardness;
    }


    public static Material createDiffuseMaterial(ImmutableVector3 color) {
        return new Material(MaterialType.DIFFUSE, color, 0, 0, 0);
    }

    public static Material createMirrorMaterial(ImmutableVector3 color) {
        return new Material(MaterialType.MIRROR, color, 0, 0, 65000.0f);
    }


    public static Material createPlasticMaterial(ImmutableVector3 color, float specularity) {
        return new Material(MaterialType.PLASTIC, color, 0, 0.f, specularity);
    }


    public static Material createEmittingMaterial(ImmutableVector3 color, float power) {
        final Material material = new Material(MaterialType.EMITTING, color, 0, power, 0.0f);
        return material;
    }

    public static Material createGlassMaterial(ImmutableVector3 transmission, float ior) {
        final Material material = new Material(MaterialType.GLASS, transmission, ior, 0, 0);
        return material;
    }


    public MaterialType getType() {
        return type;
    }


    public ImmutableVector3 getColor() {
        return color;
    }


    public ImmutableVector3 getEmittance() {
        return color.mult(power);
    }


    public float getSpecularityHardness() {
        return specularityHardness;
    }


    public float getIndexOfRefraction() {
        return indexOfRefraction;
    }
}
