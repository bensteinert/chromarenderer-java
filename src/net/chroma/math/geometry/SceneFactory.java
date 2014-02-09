package net.chroma.math.geometry;

import net.chroma.math.ImmutableVector3;
import net.chroma.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * @author steinerb
 */
public class SceneFactory {

    public static Geometry[] cornellBox(ImmutableVector3 center, float halfDimension){

        List<Geometry> result = new ArrayList<>(10);

        Vector3 shiftX = new ImmutableVector3(halfDimension, 0.0f, 0.0f);
        Vector3 shiftY = new ImmutableVector3(0.0f, halfDimension, 0.0f);
        Vector3 shiftZ = new ImmutableVector3(0.0f, 0.0f, halfDimension);


        ImmutableVector3 p0x1 = new ImmutableVector3(center.subtract(shiftX).subtract(shiftY).plus(shiftZ));
        ImmutableVector3 p1x1 = new ImmutableVector3(center.subtract(shiftX).subtract(shiftY).subtract(shiftZ));
        ImmutableVector3 p2x1 = new ImmutableVector3(center.subtract(shiftX).plus(shiftY).subtract(shiftZ));
        ImmutableVector3 p3x1 = new ImmutableVector3(center.subtract(shiftX).plus(shiftY).plus(shiftZ));

        ImmutableVector3 p0x2 = new ImmutableVector3(center.subtract(shiftX).subtract(shiftY).plus(shiftZ));
        ImmutableVector3 p1x2 = new ImmutableVector3(center.subtract(shiftX).subtract(shiftY).subtract(shiftZ));
        ImmutableVector3 p2x2 = new ImmutableVector3(center.subtract(shiftX).plus(shiftY).subtract(shiftZ));
        ImmutableVector3 p3x2 = new ImmutableVector3(center.subtract(shiftX).plus(shiftY).plus(shiftZ));

        result.add(new Triangle(p0x1,p1x1,p2x1));
        result.add(new Triangle(p3x1,p0x1,p2x1));

        ImmutableVector3 p0y1 = new ImmutableVector3(center.subtract(shiftX).subtract(shiftY).plus(shiftZ));
        ImmutableVector3 p1y1 = new ImmutableVector3(center.subtract(shiftX).subtract(shiftY).subtract(shiftZ));
        ImmutableVector3 p2y1 = new ImmutableVector3(center.subtract(shiftX).plus(shiftY).subtract(shiftZ));
        ImmutableVector3 p3y1 = new ImmutableVector3(center.subtract(shiftX).plus(shiftY).plus(shiftZ));


        result.add(new Triangle(
                new ImmutableVector3(0.f, 0.f, 1.f),
                new ImmutableVector3(.0f, 1.f, 1.f),
                new ImmutableVector3(1.f, 0.f, 1.f),
                new ImmutableVector3(0.f, 0.f, -1.f)
        ));

        return (Geometry[]) result.toArray();
    }
}
