package net.chromarenderer.math.geometry;

import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.renderer.shader.Material;
import org.junit.Test;

/**
 * @author bensteinert
 */
public class ObjectLayoutTriangleTest {

    @Test
    public void newInstance() throws Exception {
        ImmutableVector3[] arr = new ImmutableVector3[]{Vector3.FLT_MAX, Vector3.MINUS_FLT_MAX, Vector3.ONE, Vector3.Z_AXIS};
        ObjectLayoutTriangle objectLayoutTriangle = ObjectLayoutTriangle.createTriangle(arr[0],arr[1],arr[2],arr[3] , Material.NULL);
        System.out.println(objectLayoutTriangle);
    }


}