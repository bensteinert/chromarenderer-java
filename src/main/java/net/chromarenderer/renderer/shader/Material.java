package net.chromarenderer.renderer.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;

/**
 * @author bensteinert
 */
public class Material {

    public static final Material NULL = new Material(MaterialType.NULL, COLORS.BLACK, 0);
    public static final Material MIRROR = new Material(MaterialType.MIRROR, COLORS.WHITE, 0);
    private final MaterialType type;
    private final ImmutableVector3 color;
    private final float power;


    public Material(MaterialType type, ImmutableVector3 color, float power) {
        this.type = type;
        this.color = color;
        this.power = power;
    }


    public static Material createDiffuseMaterial(ImmutableVector3 color) {
        return new Material(MaterialType.DIFFUSE, color, 0.f);
    }


    public static Material createEmittingMaterial(ImmutableVector3 color, float power) {
        return new Material(MaterialType.EMITTING, color, power);
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
}
