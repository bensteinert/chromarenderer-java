package net.chromarenderer.main.javafx;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author bensteinert
 */
class ChromaFxLogWindowHandler extends Handler {

    private final BlockingQueue<LogRecord> queue;
    private ChromaFxLogWindow logWindow;


    ChromaFxLogWindowHandler() {
        this.queue = new ArrayBlockingQueue<>(1000);
    }


    @Override
    public synchronized void publish(LogRecord record) {
        final boolean success = queue.offer(record);
        if (!success) {
            System.err.println("Chroma Console Overflow! Skipping records...");
            queue.clear();
        }
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
        return logWindow != null && logWindow.isShowing() && super.isLoggable(record);
    }
}
