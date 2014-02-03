package net.chroma.main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.chroma.renderer.cores.ColorCubeRenderer;
import net.chroma.renderer.cores.MovingAverageRenderer;
import net.chroma.renderer.cores.SimpleRayTracer;

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
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case F1:
                        chroma.setRenderer(new MovingAverageRenderer(imgWidth, imgHeight));
                        break;
                    case F2:
                         chroma.setRenderer(new ColorCubeRenderer(imgWidth, imgHeight));
                        break;
                    case F3:
                        chroma.setRenderer(new SimpleRayTracer(imgWidth, imgHeight));
                        break;
                    case SPACE:
                        System.out.println("blub");
                        break;
                }
                chroma.restart();
            }
        });
        final WritableImage img = new WritableImage(imgWidth, imgHeight);

        ImageView imageView = new ImageView();
        imageView.setImage(img);
        root.getChildren().add(imageView);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                primaryStage.setTitle("Chroma2 - " + (int)chroma.getFps() + " fps");
                if(chroma.hasChanges()) {
                    img.getPixelWriter().setPixels(0, 0, imgWidth, imgHeight, PixelFormat.getByteRgbInstance(),
                            chroma.getCurrentFrame(), 0, scanlineStride);
                }
            }
        }.start();
    }

    public static void main(String[] args) {
        Thread thread = new Thread(chroma);
        thread.start();
        launch(args);
        chroma.finish();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Bye");
    }

}
