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

    private static int imgWidth = 1024;
    private static int imgHeight = 1024;
    private static int scanlineStride = imgWidth * 3;

    private static Chroma chroma = new Chroma(imgWidth, imgHeight);
    private Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
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
        Scene scene = new Scene(root, imgWidth, imgHeight);
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
        final WritableImage img = new WritableImage(imgWidth, imgHeight);

        ImageView imageView = new ImageView();
        imageView.setScaleY(-1.0);
        imageView.setImage(img);
        root.getChildren().add(imageView);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(chroma.hasChanges()) {
                    img.getPixelWriter().setPixels(0, 0, imgWidth, imgHeight, PixelFormat.getByteRgbInstance(),
                            chroma.getCurrentFrame(), 0, scanlineStride);

                }
            }
        }.start();
    }

    private EventHandler<KeyEvent> getKeyPressedEventHandler() {
        return event -> {
            switch (event.getCode()) {
                case F5:
                    chroma.init(ChromaRenderMode.AVG, imgWidth, imgHeight);
                    break;
                case F6:
                    chroma.init(ChromaRenderMode.COLOR_CUBE, imgWidth, imgHeight);
                    break;
                case F7:
                    chroma.init(ChromaRenderMode.SIMPLE, imgWidth, imgHeight);
                    break;
                case F8:
                    chroma.init(ChromaRenderMode.DISTRIBUTION, imgWidth, imgHeight);
                    break;
                case ENTER:
                    chroma.restart();
                    break;
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
