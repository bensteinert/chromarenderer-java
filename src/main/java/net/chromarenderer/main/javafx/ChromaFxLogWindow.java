package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author bensteinert
 */
public class ChromaFxLogWindow extends Stage {

    private static final Font MONACO = Font.font("Monaco", 10);
    private static final String WINDOW_TITLE = "Chroma Log";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 400;
    private static final int DRAIN_SIZE = 32;
    private final BlockingQueue<String> queue;
    private AnimationTimer animationTimer;
    private boolean follow = true;


    public ChromaFxLogWindow(BlockingQueue<String> queue) {
        super(StageStyle.UTILITY);
        this.queue = queue;
    }


    public ChromaFxLogWindow init() {
        setTitle(WINDOW_TITLE);

        HBox controls = new HBox();
        VBox vBox = new VBox();
        final TextArea logOutput = new TextArea();
        Text warning = new Text("This window can have a negative effect on rendering performance!");

        Button clearLogs = new Button("Clear Log");
        Button followLog = new Button("Follows");
        clearLogs.setOnAction(event -> logOutput.clear());
        followLog.setOnAction(event -> {
            follow = !follow;
            if (follow) {
                followLog.setText("Follows");
            } else {
                followLog.setText("No Follow");
            }
        });

        controls.getChildren().addAll(clearLogs, followLog);

        logOutput.setFont(MONACO);
        logOutput.setEditable(false);
        logOutput.setMinSize(800, 350);

        vBox.getChildren().addAll(controls, logOutput, warning);
        setScene(new Scene(vBox, WINDOW_WIDTH, WINDOW_HEIGHT));
        List<String> drain = new ArrayList<>(DRAIN_SIZE);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                queue.drainTo(drain, DRAIN_SIZE);
                if (!drain.isEmpty()) {
                    long now2 = System.currentTimeMillis();
                    if (queue.size() == DRAIN_SIZE) {
                        logOutput.appendText("Log queue is full. Entry loss is likely.");
                    }
                    double previous = logOutput.getScrollTop();
                    drain.stream().forEachOrdered(logOutput::appendText);
                    if (logOutput.getLength() > 110000) {
                        logOutput.deleteText(0, logOutput.getLength() - 100000); // 100000 characters history
                    }
                    drain.clear();
                    if (follow) {
                        logOutput.setScrollTop(Double.MAX_VALUE);
                    } else {
                        logOutput.setScrollTop(previous);
                    }

                    System.out.println("refresh took " + (System.currentTimeMillis()-now2));
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
