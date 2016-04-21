package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.chromarenderer.main.Chroma;
import net.chromarenderer.renderer.diag.ChromaStatistics;

/**
 * @author bensteinert
 */
class ChromaFxStatusWindowFactory extends StackPane {

    private static final Font MONACO = Font.font("Monaco", 14);
    private static final Font MONACO_BOLD = Font.font("Monaco", FontWeight.BOLD, 16);
    private static final String WINDOW_TITLE = "Chroma Info and Controls";
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final float WINDOW_REFRESH_INTERVAL = 1000.f;


    static Stage createStatusWindow(Chroma chroma) {

        Text intro = new Text(WINDOW_TITLE);
        intro.setFont(MONACO_BOLD);
        Text fps = new Text();
        Text totalIntersections = new Text();
        Text reverseRaysMissed = new Text();
        Text isContinuousActive = new Text();
        Text isLightSourceSamplingActive = new Text();
        Text cameraPosition = new Text();
        Text rayCount = new Text();

        VBox statusLayout = new VBox(10, intro, fps, rayCount, isContinuousActive, isLightSourceSamplingActive, reverseRaysMissed, totalIntersections, cameraPosition);

        statusLayout.getChildren().stream().filter(node -> node instanceof Text).forEach(node -> {
            ((Text) node).setFont(MONACO);
        });

        Scene secondScene = new Scene(statusLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        Stage statusStage = new Stage(StageStyle.UTILITY);

        statusStage.setTitle(WINDOW_TITLE);
        statusStage.setScene(secondScene);

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        statusStage.setX(visualBounds.getMaxX() - WINDOW_WIDTH);
        statusStage.setY(visualBounds.getMaxY() - WINDOW_HEIGHT);

        new AnimationTimer() {
            long lastTimeStamp = System.nanoTime();

            @Override
            public void handle(long now) {

                // updates just every second is fine
                float delta = (now - lastTimeStamp) / 1000000.f;
                if (delta > WINDOW_REFRESH_INTERVAL) {
                    reverseRaysMissed.setText(String.valueOf(ChromaStatistics.getReverseRaysMissedCount()));
                    rayCount.setText(String.format("Rays/ms:    %.2f", ChromaStatistics.getRayCountAndFlush() / delta));
                    totalIntersections.setText(String.format("Intersections/ms: %.2f",ChromaStatistics.getTotalIntersectionsAndFlush() / delta));
                    fps.setText(String.format("Frames/s:   %.2f [frames total: %s]", ChromaStatistics.getFps(), ChromaStatistics.getTotalFrameCount()));

                    lastTimeStamp = now;
                    cameraPosition.setText(chroma.getCamera().getPosition().toString());
                }
            }
        }.start();

        return statusStage;
    }
}
