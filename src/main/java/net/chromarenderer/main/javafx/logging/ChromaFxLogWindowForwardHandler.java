package net.chromarenderer.main.javafx.logging;

import net.chromarenderer.main.ChromaLogger;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author bensteinert
 */
public class ChromaFxLogWindowForwardHandler extends Handler {

    private final CircularFifoQueue<String> queue;
    private ChromaFxLogWindow logWindow;


    public ChromaFxLogWindowForwardHandler() {
        this.queue = new CircularFifoQueue<>(1000);
        setFormatter(new ChromaLogger.ChromaLogFormatter());
    }


    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        queue.offer(getFormatter().format(record));
    }


    @Override
    public void flush() {
    }


    @Override
    public void close() throws SecurityException {
    }


    public ChromaFxLogWindow defineNewLogWindow() {
        logWindow = new ChromaFxLogWindow(queue);
        return logWindow;
    }


    @Override
    public boolean isLoggable(LogRecord record) {
        return logWindow != null && super.isLoggable(record);
    }
}
