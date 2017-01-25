package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.chromarenderer.main.Chroma;
import net.chromarenderer.main.ChromaStatistics;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

/**
 * @author bensteinert
 */
public class ChromaFxStatusWindow extends Stage {

    private static final float WINDOW_REFRESH_INTERVAL = 1000.f; //ms
    private static final Font MONACO = Font.font("Monaco", 14);
    private static final String WINDOW_TITLE = "Chroma Perf Statistics";
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private final Chroma chroma;
    private AnimationTimer animationTimer;


    public ChromaFxStatusWindow(Chroma chroma) {
        super(StageStyle.UTILITY);
        this.chroma = chroma;
    }


    public ChromaFxStatusWindow init() {
        setTitle(WINDOW_TITLE);
        GridPane statusGrid = new GridPane();
        statusGrid.setHgap(5);
        statusGrid.setVgap(10);
        //statusGrid.setPadding(new Insets(10));

        VBox vBox = new VBox();
        vBox.getChildren().add(statusGrid);

        Text col0Title = new Text("Stats   ");
        Text col1Title = new Text("Current ");
        Text col2Title = new Text("Peak    ");
        Text col3Title = new Text("Total   ");

        Text fpsLabel = new Text("Frames/s");
        Text fpsCurr = new Text();
        Text fpsPeak = new Text();
        Text framesTotal = new Text();

        Text rayCountLabel = new Text("Rays/ms");
        Text rayCountCurr = new Text();
        Text rayCountPeak = new Text();

        Text intersectionsLabel = new Text("Intersections/ms");
        Text intersectionsCurr = new Text();
        Text intersectionsPeak = new Text();

        Text precisionFixedLabel = new Text("Imprecise Hitpoints");
        Text precisionFixedCount = new Text();

        Text L1Norm = new Text("L1 Norm");
        Text L1NormValue = new Text();

        Text cameraPosition = new Text();

        vBox.getChildren().add(cameraPosition);
        vBox.setPadding(new Insets(10));

        statusGrid.addColumn(0, col0Title, fpsLabel, intersectionsLabel, rayCountLabel, precisionFixedLabel, L1Norm);
        statusGrid.addColumn(1, col1Title, fpsCurr, intersectionsCurr, rayCountCurr, precisionFixedCount, L1NormValue);
        statusGrid.addColumn(2, col2Title, fpsPeak, intersectionsPeak, rayCountPeak);
        statusGrid.addColumn(3, col3Title, framesTotal);

        statusGrid.getChildren().stream().filter(node -> node instanceof Text).forEach(node -> {
            ((Text) node).setFont(MONACO);
        });

        setScene(new Scene(vBox, WINDOW_WIDTH, WINDOW_HEIGHT));

        animationTimer = new AnimationTimer() {
            long lastTimeStamp = System.nanoTime();
            float maxFps = 0.f;
            float maxRays = 0;
            float maxIntersections = 0;

            @Override
            public void handle(long now) {

                float delta = (now - lastTimeStamp) / 1000000.f;

                // updates just every x ms is fine
                if (delta > WINDOW_REFRESH_INTERVAL) {
                    float fps = Precision.round(ChromaStatistics.getFps(), 2);
                    float raysPerS = Precision.round(ChromaStatistics.getRayCountAndFlush() / delta, 1);
                    float intersectionsPerS = Precision.round(ChromaStatistics.getIntersectionsCountAndFlush() / delta, 1);

                    maxFps = FastMath.max(fps, maxFps);
                    maxRays = FastMath.max(raysPerS, maxRays);
                    maxIntersections = FastMath.max(intersectionsPerS, maxIntersections);

                    fpsCurr.setText(String.valueOf(fps));
                    rayCountCurr.setText(String.valueOf(raysPerS));
                    intersectionsCurr.setText(String.valueOf(intersectionsPerS));

                    fpsPeak.setText(String.valueOf(maxFps));
                    rayCountPeak.setText(String.valueOf(maxRays));
                    intersectionsPeak.setText(String.valueOf(maxIntersections));

                    precisionFixedCount.setText(String.valueOf(ChromaStatistics.getSubsurfaceCorrectionsCount()));
                    if (chroma.getCamera() != null) {
                        cameraPosition.setText(chroma.getCamera().getPosition().toString() + "\n" + chroma.getCamera().getCoordinateSystem().toString());
                    }
                    framesTotal.setText(String.valueOf(ChromaStatistics.getTotalFrameCount()));
                    lastTimeStamp = now;

                    if (chroma.getSettings() != null && chroma.getSettings().computeL1Norm()) {
                        L1NormValue.setText(String.valueOf(ChromaStatistics.L1Norm));
                    } else {
                        L1NormValue.setText("-.-");
                    }
                }
            }
        };
        animationTimer.stop();

        setOnHiding(event -> animationTimer.stop());
        setOnShowing(event -> animationTimer.start());
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
