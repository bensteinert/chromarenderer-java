package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.geometry.Geometry;

/**
 * @author bensteinert
 */
public class NoAccelerationImpl implements AccelerationStructure {

    private Iterable<? extends Geometry> geometryList;

    public NoAccelerationImpl(Iterable<? extends Geometry> geometryList) {
        this.geometryList = geometryList;
    }

    @Override
    public void intersect(IntersectionContext ctx) {
        for (Geometry geometry : geometryList) {
            ctx.checkGeometry(geometry);
        }
    }


    @Override
    public AccStructType getType() {
        return AccStructType.LIST;
    }
}
