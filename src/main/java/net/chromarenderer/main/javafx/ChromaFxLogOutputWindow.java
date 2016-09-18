package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;

/**
 * @author bensteinert
 */
public class ChromaFxLogOutputWindow extends Stage {

    private static final float WINDOW_REFRESH_INTERVAL = 500.f; //ms
    private static final Font MONACO = Font.font("Monaco", 10);
    private static final String WINDOW_TITLE = "Chroma Log";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 400;
    private final PipedInputStream stream;
    private char[] buffer = new char[8192];
    private AnimationTimer animationTimer;


    public ChromaFxLogOutputWindow(PipedInputStream stream) {
        super(StageStyle.UTILITY);
        this.stream = stream;
    }


    public ChromaFxLogOutputWindow init() {
        setTitle(WINDOW_TITLE);

        VBox vBox = new VBox();
        TextArea logOutput = new TextArea();
        logOutput.setFont(MONACO);
        logOutput.setEditable(false);

        vBox.getChildren().add(logOutput);
        setScene(new Scene(vBox, WINDOW_WIDTH, WINDOW_HEIGHT));
        long lastTimeStamp = System.nanoTime();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    float delta = (now - lastTimeStamp) / 1000000.f;
                    // updates just every x ms is fine
                    if (delta > WINDOW_REFRESH_INTERVAL && reader.ready()) {
                        reader.read(buffer, 0, 8192);
                        logOutput.appendText(new String(buffer));
                        logOutput.setScrollTop(Double.MAX_VALUE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

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
