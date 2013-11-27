package net.chroma.main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.chroma.Renderer;
import net.chroma.renderer.cores.ColorCubeRenderer;
import net.chroma.renderer.cores.RandomPixelRenderer;
import utils.FpsCounter;

public class Main extends Application {

    private FpsCounter fpsCounter;
    private Renderer renderer;
    private int imgWidth = 512;
    private int imgHeight = 512;
    private int scanlineStride;

    @Override
    public void start(final Stage primaryStage) throws Exception {

        Pane root = new Pane();
        Scene scene = new Scene(root, imgWidth, imgHeight);
        primaryStage.setScene(scene);
        primaryStage.show();

        fpsCounter = new FpsCounter();
        renderer = new RandomPixelRenderer(imgWidth, imgHeight);
        //renderer = new ColorCubeRenderer(imgWidth, imgHeight);

        scanlineStride = imgWidth * 3;

        final WritableImage img = new WritableImage(imgWidth, imgHeight);

        ImageView imageView = new ImageView();
        imageView.setImage(img);
        root.getChildren().add(imageView);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                primaryStage.setTitle("Chroma2 - " + (int)fpsCounter.fps() + " fps");
                mainLoop(img);
            }
        }.start();
    }

    private void mainLoop(WritableImage img) {
        byte[] pixels = renderer.renderNextImage(imgWidth, imgHeight);
        img.getPixelWriter().setPixels(0, 0, imgWidth, imgHeight, PixelFormat.getByteRgbInstance(), pixels, 0, scanlineStride);
    }


    public static void main(String[] args) {
        launch(args);
    }

}
