package net.chroma.main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class JavaFxMain extends Application {

    private static int imgWidth = 512;
    private static int imgHeight = 512;
    private static int scanlineStride = imgWidth * 3;

    private static Chroma2 chroma = new Chroma2(imgWidth, imgHeight);

    @Override
    public void start(final Stage primaryStage) throws Exception {

        Pane root = new Pane();
        Scene scene = new Scene(root, imgWidth, imgHeight);
        primaryStage.setScene(scene);
        primaryStage.show();

        final WritableImage img = new WritableImage(imgWidth, imgHeight);

        ImageView imageView = new ImageView();
        imageView.setImage(img);
        root.getChildren().add(imageView);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                primaryStage.setTitle("Chroma2 - " + (int)chroma.getFps() + " fps");
                img.getPixelWriter().setPixels(0, 0, imgWidth, imgHeight, PixelFormat.getByteRgbInstance(), chroma.getCurrentFrame(), 0, scanlineStride);
            }
        }.start();
    }

    public static void main(String[] args) {
        Thread thread = new Thread(chroma);
        thread.start();
        launch(args);
        chroma.finish();
    }

}
