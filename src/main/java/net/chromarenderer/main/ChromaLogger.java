package net.chromarenderer.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author bensteinert
 */
public class ChromaLogger {

    public static Logger get() {
        return Logger.getLogger("chroma");
    }


    /**
     * @author bensteinert
     */
    public static class ChromaLogFormatter extends Formatter {

        private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        @Override
        public String format(LogRecord record) {
            return String.format("%1$s %2$6s %3$s%n", dateFormat.format(new Date(record.getMillis())), record.getLevel().getName(), record.getMessage());
        }
    }
}
