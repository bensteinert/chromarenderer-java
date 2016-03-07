package net.chromarenderer.renderer.scene;

import net.chromarenderer.math.geometry.Geometry;

/**
 * Created by ben on 06/03/16.
 */
public class NoAccelerationImpl implements AccelerationStructure {

    private Iterable<? extends Geometry> geometryList;

    public NoAccelerationImpl(Iterable<? extends Geometry> geometryList) {
        this.geometryList = geometryList;
    }

    @Override
    public void getHitGeometry(GeometryScene.IntersectionContext ctx) {
        ctx.hitDistance = Float.MAX_VALUE;

        for (Geometry geometry : geometryList) {
            float distance = geometry.intersect(ctx.ray);
            if (ctx.ray.isOnRay(distance) && distance < ctx.hitDistance) {
                ctx.hitGeometry = geometry;
                ctx.hitDistance = distance;
            }
        }
    }
}
