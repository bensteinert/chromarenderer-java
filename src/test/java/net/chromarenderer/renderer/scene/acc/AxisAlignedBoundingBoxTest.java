package net.chromarenderer.renderer.scene.acc;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.raytracing.Ray;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author steinerb
 */
public class AxisAlignedBoundingBoxTest {

    @Test
    public void getCenter() throws Exception {
        AxisAlignedBoundingBox bBox = new AxisAlignedBoundingBox(new ImmutableVector3(3.f,1.f,0.f), new ImmutableVector3(5.f,3.f,2.f));
        Assert.assertEquals(bBox.getCenter(), new ImmutableVector3(4.f,2.f,1.f));

        AxisAlignedBoundingBox bBox2 = new AxisAlignedBoundingBox(new ImmutableVector3(-3.f,-1.f,0.f), new ImmutableVector3(5.f,3.f,-2.f));
        Assert.assertEquals(bBox2.getCenter(), new ImmutableVector3(1.f,1.f,-1.f));
    }


    @Test
    public void intersects() throws Exception {
        AxisAlignedBoundingBox bBox = new AxisAlignedBoundingBox(new ImmutableVector3(3.f,1.f,0.f), new ImmutableVector3(5.f, 3.f, 2.f));
        IntersectionContext ctx = new IntersectionContext();

        ctx.ray = new Ray(Vector3.ORIGIN, bBox.getCenter().normalize());
        Assert.assertTrue("Origin-to-center does not intersect although it should!", bBox.intersects(ctx));

        ctx.ray = new Ray(new ImmutableVector3(4.f,0.f,1.f), Vector3.Y_AXIS);
        Assert.assertTrue("BelowBox-Up does not intersect although it should!", bBox.intersects(ctx));

        ctx.ray = new Ray(new ImmutableVector3(4.f,10.f,1.f), new ImmutableVector3(0.0f, -1.0f, 0.0f));
        Assert.assertTrue("AboveBox-Down does not intersect although it should!", bBox.intersects(ctx));

        ctx.ray = new Ray(new ImmutableVector3(10.f,3.f,1.f), new ImmutableVector3(-1.0f, 0.0f, 0.0f));
        Assert.assertTrue("Right-to-Left does not intersect although it should!", bBox.intersects(ctx));

        ctx.ray = new Ray(new ImmutableVector3(4.f,2.f,-10.f), new ImmutableVector3(0.0f, 0.0f, 1.0f));
        Assert.assertTrue("Front-to-Back does not intersect although it should!", bBox.intersects(ctx));

        ctx.ray = new Ray(Vector3.ORIGIN, new ImmutableVector3(5.f,1.0f,0.f).normalize());
        Assert.assertTrue("Origin-to-edgePoint does not intersect although it should!", bBox.intersects(ctx));

        ctx.ray = new Ray(bBox.getCenter(), Vector3.ORIGIN.minus(bBox.getCenter()).normalize());
        Assert.assertTrue("Center-to-outside does not intersect although it should!", bBox.intersects(ctx));

        ctx.ray = new Ray(Vector3.ORIGIN, new ImmutableVector3(5.f,0.99f,0.f).normalize());
        Assert.assertFalse("Origin-to-closeToEdge point intersects although it shouldn't!", bBox.intersects(ctx));

        ctx.ray = new Ray(new ImmutableVector3(-1.f, 1.f, 0.f), new ImmutableVector3(-1.f, 0.f, 0.f));
        Assert.assertFalse("Does intersect although it shouldn't!", bBox.intersects(ctx));

        ctx.ray = new Ray(new ImmutableVector3(10.f,10.f,10.f), new ImmutableVector3(0.0f, -1.0f, 0.0f));
        Assert.assertFalse("AboveBox-Down-Passing does intersect although it shouldn't!", bBox.intersects(ctx));

        ctx.ray = new Ray(new ImmutableVector3(6.f,2.f,-10.f), new ImmutableVector3(0.0f, 0.0f, 1.0f));
        Assert.assertFalse("Front-to-Back does intersect although it shouldn't!", bBox.intersects(ctx));

    }

}