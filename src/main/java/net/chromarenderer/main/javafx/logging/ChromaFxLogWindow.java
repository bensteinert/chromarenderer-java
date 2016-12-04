package net.chromarenderer.main.javafx.logging;

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
import org.apache.commons.collections4.queue.CircularFifoQueue;

/**
 * @author bensteinert
 */
public class ChromaFxLogWindow extends Stage {

    private static final Font MONACO = Font.font("Monaco", 10);
    private static final String WINDOW_TITLE = "Chroma Log";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 400;
    private final CircularFifoQueue<String> queue;
    private AnimationTimer animationTimer;
    private boolean follow = true;


    public ChromaFxLogWindow(CircularFifoQueue<String> queue) {
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

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(!queue.isEmpty()) {
                    if (queue.isAtFullCapacity()) {
                        logOutput.appendText("Log queue is full. Entry loss is likely.\n");
                    }
                    int elementsToProcess = Math.min(50, queue.size());
                    double previous = logOutput.getScrollTop();
                    while (elementsToProcess-- > 0) {
                        logOutput.appendText(queue.remove());
                    }
                    if (logOutput.getLength() > 110000) {
                        logOutput.deleteText(0, logOutput.getLength() - 100000); // 100000 characters history
                    }

                    if (follow) {
                        logOutput.setScrollTop(Double.MAX_VALUE);
                    } else {
                        logOutput.setScrollTop(previous);
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
