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
import net.chroma.renderer.cores.RandomPixelGenerator;
import utils.FpsCounter;

public class Main extends Application {

    private FpsCounter fpsCounter;
    private Renderer renderer;
    private int imgWidth = 512;
    private int imgHeight = 512;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Pane root = new Pane();
        Scene scene = new Scene(root, 512, 512);
        primaryStage.setScene(scene);
        primaryStage.show();

        fpsCounter = new FpsCounter();
        renderer = new RandomPixelGenerator();

        WritableImage img = new WritableImage(512, 512);

        ImageView imageView = new ImageView();
        imageView.setImage(img);
        root.getChildren().add(imageView);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                primaryStage.setTitle("Chroma2 - " + fpsCounter.fps() + "fps");
                mainLoop(img);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }.start();
    }

    private void mainLoop(WritableImage img) {
        byte[] pixels = renderer.renderNextImage(imgWidth, imgHeight);
        img.getPixelWriter().setPixels(0, 0, imgWidth, imgHeight, PixelFormat.getByteRgbInstance(), pixels, 0, 0);
    }


    public static void main(String[] args) {
        launch(args);
    }

}
