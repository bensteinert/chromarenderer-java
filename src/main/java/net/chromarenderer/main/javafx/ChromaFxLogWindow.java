package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.LogRecord;

/**
 * @author bensteinert
 */
public class ChromaFxLogWindow extends Stage {

    private static final float WINDOW_REFRESH_INTERVAL = 100.f; //ms
    private static final Font MONACO = Font.font("Monaco", 10);
    private static final String WINDOW_TITLE = "Chroma Log";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 400;
    private final BlockingQueue<LogRecord> queue;
    private AnimationTimer animationTimer;
    ChromaLogFormatter logFormatter = new ChromaLogFormatter();


    public ChromaFxLogWindow(BlockingQueue<LogRecord> queue) {
        super(StageStyle.UTILITY);
        this.queue = queue;
    }


    public ChromaFxLogWindow init() {
        setTitle(WINDOW_TITLE);

        VBox vBox = new VBox();
        TextArea logOutput = new TextArea();
        logOutput.setFont(MONACO);
        logOutput.setEditable(false);

        vBox.getChildren().add(logOutput);
        setScene(new Scene(vBox, WINDOW_WIDTH, WINDOW_HEIGHT));
        long lastTimeStamp = System.nanoTime();
        List<LogRecord> drain = new ArrayList<>(10);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                float delta = (now - lastTimeStamp) / 1000000.f;
                // updates just every x ms is fine
                if (delta > WINDOW_REFRESH_INTERVAL) {
                    queue.drainTo(drain, 10);
                    if (!drain.isEmpty()) {
                        drain.stream().forEachOrdered(record -> logOutput.appendText(logFormatter.format(record)));
                        logOutput.setScrollTop(Double.MAX_VALUE);
                        drain.clear();
                    }
                }
            }
        };

        setOnShowing(event -> {
            animationTimer.start();
        });

        setOnHiding(event -> {
            animationTimer.stop();
        });

        return this;
    }


    void start() {
        if (isShowing()) {
            animationTimer.start();
        }
    }


    void stop() {
        animationTimer.stop();
    }

}
