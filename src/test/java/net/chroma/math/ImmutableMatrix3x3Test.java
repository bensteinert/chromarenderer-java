package net.chroma.math;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author steinerb
 */
public class ImmutableMatrix3x3Test {


    @Test
    public void testInvert() throws Exception {

    }

    @Test
    public void testOrthogonalize() throws Exception {

    }

    @Test
    public void testTranspose() throws Exception {
        ImmutableMatrix3x3 testee = new ImmutableMatrix3x3(1.f, 2.f, 3.f, 4.f, 5.f, 6.f, 7.f, 8.f, 9.f);
        ImmutableMatrix3x3 expected = new ImmutableMatrix3x3(1.f, 4.f, 7.f, 2.f, 5.f, 8.f, 3.f, 6.f, 9.f);
        Assert.assertEquals(expected, testee.transpose());
    }

    @Test
    public void testMult() throws Exception {

    }

    @Test
    public void testMult1() throws Exception {

    }
}
