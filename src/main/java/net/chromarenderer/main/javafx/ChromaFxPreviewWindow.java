package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.chromarenderer.main.Chroma;

/**
 * @author bensteinert
 */
public class ChromaFxPreviewWindow extends Stage {

    private final Chroma chroma;
    private final int width;
    private final int height;
    private AnimationTimer animationTimer;


    public ChromaFxPreviewWindow(Chroma chroma, int width, int height) {
        super(StageStyle.UTILITY);
        this.chroma = chroma;
        this.width = width;
        this.height = height;
    }

    public ChromaFxPreviewWindow init() {
        Pane previewPane = new Pane();
        final WritableImage img = new WritableImage(width, height);

        ImageView imageView = new ImageView();
        imageView.setScaleY(-1.0);
        imageView.setImage(img);
        previewPane.getChildren().add(imageView);

        // TODO: parallelize?
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (chroma.hasChanges()) {
                    img.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance(), chroma.getCurrentFrame(), 0, height * 3);
                }
            }
        };
        animationTimer.stop();

        setOnHiding(event -> animationTimer.stop());
        setOnShowing(event -> animationTimer.start());

        setTitle("Chroma Preview");
        setScene(new Scene(previewPane, width, height));
        return this;
    }


    void start() {
        animationTimer.start();
    }


    void stop() {
        animationTimer.stop();
    }
}
