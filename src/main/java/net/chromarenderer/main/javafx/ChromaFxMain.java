package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.chromarenderer.main.Chroma;
import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.scene.acc.AccStructType;
import net.chromarenderer.utils.BufferPressedKeysEventHandler;

import java.util.Set;

public class ChromaFxMain extends Application {

    private static final Chroma chroma = new Chroma();

    private static ChromaSettings settings;

    private Stage previewStage;
    private Stage utilityStage;


    @Override
    public void start(final Stage chromaMainStage) throws Exception {

        BorderPane mainBox = new BorderPane();
        GridPane controlPane = new GridPane();
        controlPane.setHgap(10);
        controlPane.setVgap(10);
        controlPane.setPadding(new Insets(0, 10, 0, 10));

        controlPane.add(new Text("Acc Struct:"), 0, 0);
        ComboBox<AccStructType> accStructCombo = new ComboBox<>(FXCollections.observableArrayList(
                AccStructType.LIST,
                AccStructType.AABB_BVH
        ));
        controlPane.add(accStructCombo, 1, 0);

        controlPane.add(new Text("Render Mode:"), 0, 1);
        ComboBox<ChromaRenderMode> renderModeCombo = new ComboBox<>(FXCollections.observableArrayList(
                ChromaRenderMode.SIMPLE,
                ChromaRenderMode.PTDL,
                ChromaRenderMode.COLOR_CUBE,
                ChromaRenderMode.AVG
        ));
        controlPane.add(renderModeCombo, 1, 1);

        controlPane.add(new Text("Accumulate:"), 0, 2);
        CheckBox accumulate = new CheckBox();
        controlPane.add(accumulate, 1, 2);

        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(5));
        Button applySettings = new Button("Apply Settings");
        applySettings.setOnAction(event -> {
            settings = settings.changeContinuousRender(accumulate.selectedProperty().getValue());
            settings = settings.changeAccStructMode(accStructCombo.getValue());
            settings = settings.changeMode(renderModeCombo.getValue());
            chroma.reinit(settings);
        });
        Button start = new Button("Start");
        start.setOnAction(event -> {
            chroma.start();
        });
        Button screenShot = new Button("Save Image");
        screenShot.setOnAction(event -> chroma.takeScreenShot());
        Button stop = new Button("Stop");
        stop.setOnAction(event -> chroma.stop());

        buttonPane.getChildren().add(applySettings);
        buttonPane.getChildren().add(start);
        buttonPane.getChildren().add(screenShot);
        buttonPane.getChildren().add(stop);

        mainBox.setCenter(controlPane);
        mainBox.setBottom(buttonPane);

        chromaMainStage.setX(0);
        chromaMainStage.setX(0);
        chromaMainStage.setTitle("Chroma Renderer");
        Scene scene = new Scene(mainBox, settings.getImgWidth(), settings.getImgHeight());
        chromaMainStage.setScene(scene);
        BufferPressedKeysEventHandler bufferPressedKeysEventHandler = new BufferPressedKeysEventHandler();
        chromaMainStage.addEventHandler(KeyEvent.ANY, bufferPressedKeysEventHandler);

        previewStage = ChromaFxPreviewWindowFactory.createPreviewWindow(chroma);
        utilityStage = ChromaFxStatusWindowFactory.createStatusWindow(chroma);

        chromaMainStage.setOnCloseRequest((arg0 -> {
            arg0.consume();
            utilityStage.close();
            previewStage.close();
            chromaMainStage.close();
        }));

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                Set<KeyCode> pressedKeys = bufferPressedKeysEventHandler.getPressedKeys();
                chroma.moveCamera(getTranslationVector(pressedKeys), getRotationVector(pressedKeys));
            }
        }.start();

        chromaMainStage.toFront();
        chromaMainStage.show();
        previewStage.show();
        utilityStage.show();
    }


    private Vector3 getRotationVector(Set<KeyCode> pressedKeys) {
        float rotX = 0.0f;
        float rotY = 0.0f;
        //float rotZ = 0.0f;

        for (KeyCode pressedKey : pressedKeys) {
            switch (pressedKey) {
                case NUMPAD7:
                    rotY -= 0.02f;
                    break;
                case NUMPAD9:
                    rotY += 0.02f;
                    break;
                case NUMPAD1:
                    rotX -= 0.02f;
                    break;
                case NUMPAD3:
                    rotX += 0.02f;
                    break;
            }
        }

        return new ImmutableVector3(rotX, rotY, 0.0f);
    }


    private Vector3 getTranslationVector(Set<KeyCode> pressedKeys) {

        float moveX = 0.0f;
        float moveY = 0.0f;
        float moveZ = 0.0f;

        // camera along negative z axis!
        for (KeyCode pressedKey : pressedKeys) {
            switch (pressedKey) {
                case NUMPAD4:
                case A:
                    moveX -= 0.1f;
                    break;
                case NUMPAD6:
                case D:
                    moveX += 0.1f;
                    break;
                case NUMPAD8:
                case W:
                    moveZ -= 0.1f;
                    break;
                case NUMPAD2:
                case S:
                    moveZ += 0.1f;
                    break;
                case PAGE_DOWN:
                case Q:
                    moveY -= 0.1f;
                    break;
                case PAGE_UP:
                case E:
                    moveY += 0.1f;
                    break;
            }
        }

        return new ImmutableVector3(moveX, moveY, moveZ);
    }


    public static void main(String[] args) {
        Thread thread = new Thread(chroma);
        settings = new ChromaSettings(512, 512, ChromaRenderMode.PTDL, true, 2, true, AccStructType.LIST);
        chroma.reinit(settings);
        thread.start();

        launch(ChromaFxMain.class, args);
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
