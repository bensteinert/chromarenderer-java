package net.chromarenderer.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author bensteinert
 */
public class WorkspaceUtils {

    static final String BLENDER_TO_CHROMA_SCRIPT = "blenderToChroma.py";
    static final String CHROMA_WORK_DIR = ".chroma";

    static Path ensureAndGetPythonScript(Path chromaWorkFolderPath) throws IOException {
        Path pythonScript = chromaWorkFolderPath.resolve(BLENDER_TO_CHROMA_SCRIPT);

        if (!Files.exists(pythonScript)) {
            Files.copy(WorkspaceUtils.class.getResourceAsStream("/" + BLENDER_TO_CHROMA_SCRIPT), pythonScript);
        }
        return pythonScript;
    }


    static Path ensureAndGetChromaWorkFolderPath() throws IOException {
        final Path chromaWorkFolderPath = Paths.get(System.getProperty("user.home") + File.separator + CHROMA_WORK_DIR);
        if (!Files.exists(chromaWorkFolderPath)) {
            Files.createDirectory(chromaWorkFolderPath);
        }
        return chromaWorkFolderPath;
    }
}

