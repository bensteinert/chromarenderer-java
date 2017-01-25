package net.chromarenderer.math;

import org.junit.Assert;
import org.junit.Test;

public class ImmutableVector3Test {


    @Test
    public void testGetMinValueIndex() throws Exception {
        ImmutableVector3 vec1 = new ImmutableVector3(1.f,2.f,3.f);
        Assert.assertEquals(0, vec1.getMinValueIndex());

        ImmutableVector3 vec2 = new ImmutableVector3(3.f,1.f,2.f);
        Assert.assertEquals(1, vec2.getMinValueIndex());

        ImmutableVector3 vec3 = new ImmutableVector3(2.f,3.f,1.f);
        Assert.assertEquals(2, vec3.getMinValueIndex());
    }

    @Test
    public void testGetMaxValueIndex() throws Exception {
        ImmutableVector3 vec1 = new ImmutableVector3(1.f,2.f,3.f);
        Assert.assertEquals(2, vec1.getMaxValueIndex());

        ImmutableVector3 vec2 = new ImmutableVector3(3.f,1.f,2.f);
        Assert.assertEquals(0, vec2.getMaxValueIndex());

        ImmutableVector3 vec3 = new ImmutableVector3(2.f,3.f,1.f);
        Assert.assertEquals(1, vec3.getMaxValueIndex());
    }
}