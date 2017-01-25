package net.chromarenderer.utils;

import net.chromarenderer.main.ChromaLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

/**
 * @author bensteinert
 */
public class WorkspaceUtils {

    private static final Logger LOGGER = ChromaLogger.get();
    private static final String BLENDER_TO_CHROMA_SCRIPT = "blenderToChroma.py";
    private static final String CHROMA_WORK_DIR = ".chroma";

    static Path ensureAndGetPythonScript(Path chromaWorkFolderPath) throws IOException {
        Path pythonScript = chromaWorkFolderPath.resolve(BLENDER_TO_CHROMA_SCRIPT);

        if (!Files.exists(pythonScript)) {
            Files.copy(getPackagedPythonScriptAsStream(), pythonScript);
        } else {
            boolean forceOverwrite = false;
            int currentVersion = -1;
            int packagedScriptVersion = -1;

            try {
                currentVersion = getCurrentVersion(pythonScript);
            }
            catch (NumberFormatException ex) {
                LOGGER.severe("Unable to derive blenderToChroma script version of the current file. File corruption assumed. Will override");
                forceOverwrite = true;
            }

            try {
                packagedScriptVersion = getPackagedVersion();
            }
            catch (NumberFormatException ex) {
                LOGGER.severe("Unable to derive blenderToChroma script version of the packaged file. File corruption assumed. Copy job aborts.");
                //do nothing
            }

            // version has to exactly match (also takes version downgrade into account. useful for dev)
            if (forceOverwrite || packagedScriptVersion != currentVersion) {
                Files.copy(getPackagedPythonScriptAsStream(), pythonScript, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("blenderToChroma script updated in workspace folder.");
            }
        }

        return pythonScript;
    }

    private static int getPackagedVersion() throws IOException {
        final String versionString = new BufferedReader(new InputStreamReader(getPackagedPythonScriptAsStream())).readLine();
        return Integer.valueOf(versionString.replace("#", " ").trim());
    }


    private static InputStream getPackagedPythonScriptAsStream() {
        return WorkspaceUtils.class.getResourceAsStream("/" + BLENDER_TO_CHROMA_SCRIPT);
    }


    private static int getCurrentVersion(Path pythonScript) throws IOException {
        final String versionString = new BufferedReader(new FileReader(pythonScript.toFile())).readLine();
        return Integer.valueOf(versionString.replace("#", " ").trim());
    }


    static Path ensureAndGetChromaWorkFolderPath() throws IOException {
        final Path chromaWorkFolderPath = Paths.get(System.getProperty("user.home") + File.separator + CHROMA_WORK_DIR);
        if (!Files.exists(chromaWorkFolderPath)) {
            Files.createDirectory(chromaWorkFolderPath);
        }
        return chromaWorkFolderPath;
    }
}

