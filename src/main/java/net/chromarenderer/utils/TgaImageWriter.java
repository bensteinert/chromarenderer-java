package net.chromarenderer.utils;

import net.chromarenderer.main.ChromaLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


public class TgaImageWriter {

    public static void writeTga(byte[] pixels, int width, int height, String fileName) {

        if (width > Short.MAX_VALUE || height > Short.MAX_VALUE){
            System.err.println("Image dimensions are not supported by TGA format!");
            return;
        }

        OutputStream outputStream = null;
        try {
            final Path path = WorkspaceUtils.ensureAndGetChromaWorkFolderPath();
            outputStream = Files.newOutputStream(path.resolve(fileName));

            // TGA file header: http://en.wikipedia.org/wiki/Truevision_TGA
            byte[] header = new byte[18];
            header[2] = 2; // grayscale or RGB without RLE
            header[12] = (byte) width; // width as short
            header[13] = (byte) (width >> 8);
            header[14] = (byte) height; // height as short
            header[15] = (byte) (height >> 8);
            header[16] = 24; // BPP

            byte[] currentFrame = Arrays.copyOf(pixels, pixels.length);

            try {
                outputStream.write(header);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int y = height-1; y >= 0; y--) { // flip vertically
                int yOffset = y*width*3;

                for (int x = 0; x < width; x++) { // swap RGB => BGR
                    int inLineOffset = yOffset + x * 3 ;
                    byte tmp = currentFrame[inLineOffset];
                    currentFrame[inLineOffset] = currentFrame[inLineOffset + 2];
                    currentFrame[inLineOffset + 2] = tmp;
                }
            }

            outputStream.write(currentFrame);
            outputStream.close();
            ChromaLogger.get().info("Successfully stored screenshot to " + path + fileName);

        } catch (IOException e) {
            e.printStackTrace();
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
