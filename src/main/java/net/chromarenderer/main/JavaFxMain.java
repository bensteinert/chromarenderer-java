package net.chromarenderer.main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.diag.ChromaStatistics;

public class JavaFxMain extends Application {

    private static final Chroma chroma = new Chroma();
    private static ChromaSettings settings;

    private final Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
    private Stage utilityStage;


    @Override
    public void start(final Stage primaryStage) throws Exception {
        mainRenderWindow(primaryStage);
        utilityStage = statusWindow();
    }

    private Stage statusWindow() {
        StackPane secondaryLayout = new StackPane();

        Font monaco = Font.font("Monaco", 14);
        Text fps = new Text();
        fps.setFont(monaco);
        Text reverseRaysMissed = new Text();
        reverseRaysMissed.setFont(monaco);
        Text isContinuousActive = new Text();
        isContinuousActive.setFont(monaco);
        Text rayCount = new Text();
        rayCount.setFont(monaco);

        VBox vbox = new VBox(10, fps, rayCount, isContinuousActive, reverseRaysMissed);
        secondaryLayout.getChildren().add(vbox);

        Scene secondScene = new Scene(secondaryLayout, 400, 400);
        Stage secondStage = new Stage(StageStyle.UTILITY);
        secondStage.setTitle("Chroma Info and Controls");
        secondStage.setScene(secondScene);

        secondStage.setX(visualBounds.getMaxX() - 400);
        secondStage.setY(visualBounds.getMaxY() - 400);
        secondStage.show();

        new AnimationTimer() {
            long lastTimeStamp = System.nanoTime();
            @Override
            public void handle(long now) {

                // updates just every second is fine
                float delta = (now - lastTimeStamp) / 1000000.f;
                if(delta > 1000.f) {
                    ChromaStatistics statistics = chroma.getStatistics();
                    reverseRaysMissed.setText(String.valueOf(statistics.getReverseRaysMissedCount()));
                    reverseRaysMissed.setText(String.valueOf(statistics.getReverseRaysMissedCount()));
                    rayCount.setText(        String.format("Rays/ms:    %.2f", statistics.getRayCountAndFlush() / delta));
                    fps.setText(             String.format("Frames/s:   %.2f [frames total: %s]", statistics.getFps(), statistics.getTotalFrameCount()));
                    lastTimeStamp = now;
                }

                isContinuousActive.setText(String.format("Continuous: %s", Boolean.toString(settings.isForceContinuousRender())));
            }
        }.start();

        return secondStage;
    }

    private void mainRenderWindow(final Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, settings.getImgWidth(), settings.getImgHeight());
        primaryStage.setScene(scene);
        primaryStage.setX(visualBounds.getMinX());
        primaryStage.setX(visualBounds.getMinY());
        primaryStage.setTitle("Chroma");
        primaryStage.show();
        primaryStage.toFront();

        primaryStage.setOnCloseRequest((arg0 -> {
            arg0.consume();
            utilityStage.close();
            primaryStage.close();
        }));

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, getKeyPressedEventHandler());
        final WritableImage img = new WritableImage(settings.getImgWidth(), settings.getImgHeight());

        ImageView imageView = new ImageView();
        imageView.setScaleY(-1.0);
        imageView.setImage(img);
        root.getChildren().add(imageView);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(chroma.hasChanges()) {
                    img.getPixelWriter().setPixels(0, 0, settings.getImgWidth(), settings.getImgHeight(),
                            PixelFormat.getByteRgbInstance(), chroma.getCurrentFrame(), 0, settings.getImgHeight() * 3);

                }
            }
        }.start();
    }

    private EventHandler<KeyEvent> getKeyPressedEventHandler() {
        return event -> {
            boolean reinitNeeded = false;
            switch (event.getCode()) {
                case C:
                    settings = settings.changeContinuousRender(!settings.isForceContinuousRender());
                    reinitNeeded = true;
                    break;
                case F5:
                    settings = settings.changeMode(ChromaRenderMode.AVG);
                    reinitNeeded = true;
                    break;
                case F6:
                    settings = settings.changeMode(ChromaRenderMode.COLOR_CUBE);
                    reinitNeeded = true;
                    break;
                case F7:
                    settings = settings.changeMode(ChromaRenderMode.SIMPLE);
                    reinitNeeded = true;
                    break;
                case F8:
                    settings = settings.changeMode(ChromaRenderMode.SIMPLE_PT);
                    reinitNeeded = true;
                    break;
                case ENTER:
                    chroma.restart();
                    break;
            }

            if (reinitNeeded) {
                chroma.init(settings);
                reinitNeeded = false;
            }
        };
    }

    public static void main(String[] args) {
        Thread thread = new Thread(chroma);
        settings = new ChromaSettings(1024, 1024, ChromaRenderMode.SIMPLE, false);
        chroma.init(settings);

        thread.start();
        launch(JavaFxMain.class, args);
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Bye");
    }

}
