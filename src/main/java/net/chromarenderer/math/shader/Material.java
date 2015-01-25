package net.chromarenderer.math.shader;

import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.Vector3;

/**
 * @author steinerb
 */
public class Material {

    public static final Material NULL = new Material(MaterialType.NULL, COLORS.BLACK);
    private final MaterialType type;
    private final Vector3 color;


    public Material(MaterialType type, Vector3 color) {
        this.type = type;
        this.color = color;
    }


    public MaterialType getType() {
        return type;
    }


    public Vector3 getColor() {
        return color;
    }
}
