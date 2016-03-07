package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;

/**
 * Created by ben on 07/03/16.
 */
public class AxisAlignedBoundingBox {

    private final ImmutableVector3 pMin;
    private final ImmutableVector3 pMax;

    public AxisAlignedBoundingBox(ImmutableVector3 pMin, ImmutableVector3 pMax) {
        this.pMin = pMin;
        this.pMax = pMax;
    }

    public Vector3 getCenter() {
        return pMin.plus(pMax.minus(pMin).mult(0.5f));
    }
}
