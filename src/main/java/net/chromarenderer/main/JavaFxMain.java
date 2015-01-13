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
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.diag.ChromaStatistics;

public class JavaFxMain extends Application {

    private static final ChromaSettings settings;
    private static final Chroma chroma;

    static {
        settings = new ChromaSettings();
        chroma = new Chroma();
        chroma.init(settings);
    }

    private final Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
    private Stage utilityStage;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        mainRenderWindow(primaryStage);
        utilityStage = statusWindow();
    }

    private Stage statusWindow() {
        StackPane secondaryLayout = new StackPane();

        Text fps = new Text();
        Text reverseRaysMissed = new Text();
        VBox vbox = new VBox(20, fps, reverseRaysMissed);
        secondaryLayout.getChildren().add(vbox);

        Scene secondScene = new Scene(secondaryLayout, 400, 400);
        Stage secondStage = new Stage(StageStyle.UTILITY);
        secondStage.setTitle("Chroma Info and Controls");
        secondStage.setScene(secondScene);

        secondStage.setX(visualBounds.getMaxX() - 400);
        secondStage.setY(visualBounds.getMaxY() - 400);
        secondStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                ChromaStatistics statistics = chroma.getStatistics();
                fps.setText(String.format("%.2f FPS",statistics.getFps()));
                reverseRaysMissed.setText(String.valueOf(statistics.getReverseRaysMissedCount()));
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
                case F5:
                    settings.setMode(ChromaRenderMode.AVG);
                    reinitNeeded = true;
                    break;
                case F6:
                    settings.setMode(ChromaRenderMode.COLOR_CUBE);
                    reinitNeeded = true;
                    break;
                case F7:
                    settings.setMode(ChromaRenderMode.SIMPLE);
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
