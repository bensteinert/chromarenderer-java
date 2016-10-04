package net.chromarenderer.main.javafx;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author bensteinert
 */
class ChromaFxLogWindowForwardHandler extends Handler {

    private final BlockingQueue<String> queue;
    private ChromaFxLogWindow logWindow;


    ChromaFxLogWindowForwardHandler() {
        this.queue = new ArrayBlockingQueue<>(1000);
        setFormatter(new ChromaLogFormatter());
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
        return logWindow != null && logWindow.isShowing() && super.isLoggable(record);
    }
}
