package net.chromarenderer.main.javafx;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * @author bensteinert
 */
public class ChromaFxConsoleLogHandler extends StreamHandler {


    public ChromaFxConsoleLogHandler(PipedOutputStream pipedOutputStream) throws IOException {
        super(pipedOutputStream, new ChromaLogFormatter());
        //setLevel(Level.FINE);
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

}
