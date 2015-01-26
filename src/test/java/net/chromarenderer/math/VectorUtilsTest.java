package net.chromarenderer.math;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class VectorUtilsTest extends TestCase {

    @Test
    public void testMirror() throws Exception {
        ImmutableVector3 normal = new ImmutableVector3(0.f, 1.f, 0.f);
        ImmutableVector3 toMirror = new ImmutableVector3(-1.f, 1.f, 0.f).normalize();
        Vector3 result = VectorUtils.mirror(toMirror, normal);
        Assert.assertEquals(result, new ImmutableVector3(1.f, 1.f, 0.f).normalize());
    }
}