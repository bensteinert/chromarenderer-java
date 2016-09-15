package net.chromarenderer.main.javafx;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author bensteinert
 */
public class ChromaLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return String.format("%1d %2$6s %3$s%n", record.getMillis(), record.getLevel().getName(), record.getMessage());
    }
}
