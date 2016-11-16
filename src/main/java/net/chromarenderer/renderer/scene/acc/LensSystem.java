package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Disk;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.renderer.camera.Aperture;

/**
 * @author bensteinert
 */
public class LensSystem implements AccelerationStructure {

    /**
     * Lens surfaces from scene entrance surface to sensor.
     */
    private final Geometry[] surfaces;

    private final Aperture[] apertures;

    /**
     * Projection of the image side lens on the scene side and vice versa. Usually, both projections are obstructed by
     * one or more apertures inside the LensSystem
     */
    private final Disk[] entrancePupils;

    /**
     * Decrease aperture diameter
     **/
    public void stopUp() {

    }

    /**
     * Increase aperture diameter
     **/
    public void stopDown() {

    }

    public Vector3 getFrontSample() {
        return surfaces[0].getUnifDistrSample();
    }

    public Vector3 getBackSample() {
        return surfaces[surfaces.length - 1].getUnifDistrSample();
    }


    @Override
    public void intersect(IntersectionContext ctx) {

    }

    @Override
    public AccStructType getType() {
        return AccStructType.LENS_SYSTEM;
    }
}
