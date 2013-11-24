//package net.chroma.renderer.cores;
//
//import net.chroma.math.Vector3;
//
///**
// * @author steinerb
// */
//public class ForkedRandomPixelGenerator extends RandomPixelRenderer {
//
//    public ForkedRandomPixelGenerator(int imgWidth, int imgHeight) {
//        super(imgWidth, imgHeight);
//    }
//
//    @Override
//    public byte[] renderNextImage(int imgWidth, int imgHeight) {
//       // byte[] bytes = random8BitPixels(imgWidth, imgHeight);
//        Vector3[] vector3s = randomFloatPixels(imgWidth, imgHeight);
//        buffer.accumulate(vector3s);
//        return buffer.toByteImage();
//    }
//}
