package net.chromarenderer.utils;

import net.chromarenderer.main.ChromaLogger;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.renderer.scene.acc.LensSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author bensteinert
 */
public class LensSystemImporter {

    public static LensSystem importFromFile(Path path, String lensFileName) {
        // TODO: parse given lens definition file

        Path lensDefinitionFile = path.resolve(lensFileName);

        final File file = lensDefinitionFile.toFile();

//        if (!Files.exists(file)) {
//            ChromaLogger.get().log(Level.SEVERE, "Lens definition file " + lensDefinitionFile.toString() + " not found. Aborting.");
//        }

        final BufferedReader reader;
        try {
            reader = Files.newBufferedReader(lensDefinitionFile);

        final List<Geometry> imported = new ArrayList<>();

        reader.lines().forEach(line -> {
            if (!line.startsWith("#")) {
                imported.add(readLensLine(line));
            }
        });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Geometry readLensLine(String input) {
        String[] cols = input.split("/w");

        // TODO: split up line, count cols and decide what type of surface it is
        switch (cols.length) {
            case 3: // aperture
                float thickness = Float.valueOf(cols[0]);
                float radius = Float.valueOf(cols[1]);
                float numberOfBlades = Float.valueOf(cols[2]);
                break;
            case 5: // plane surface
                break;
            case 6: // curved surface (of 2nd order -> sphere/cap)
                break;
        }
        return null;
    }
}
