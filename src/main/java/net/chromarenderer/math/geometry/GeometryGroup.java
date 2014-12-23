//package net.chroma.math.geometry;
//
//import net.chromarenderer.math.ImmutableArrayMatrix3x3;
//import net.chromarenderer.math.Vector3;
//import net.chromarenderer.math.raytracing.Ray;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//
///**
// * @author steinerb
// */
//public class GeometryGroup implements Geometry {
//
//    Geometry[] group;
//
//    public GeometryGroup(Geometry ... input) {
//        group = input;
//    }
//
//    @Override
//    public float intersect(Ray ray) {
//        throw new NotImplementedException();
//    }
//
//    @Override
//    public Geometry transpose(Vector3 transpose) {
//
//        for (Geometry geometry : group) {
//            geometry.transpose()
//        }
//    }
//
//    @Override
//    public Geometry rotate(ImmutableArrayMatrix3x3 rotationY) {
//        return null;
//    }
//}
