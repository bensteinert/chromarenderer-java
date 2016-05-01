package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.chromarenderer.main.Chroma;
import net.chromarenderer.main.ChromaSettings;
import net.chromarenderer.math.COLORS;
import net.chromarenderer.math.ImmutableVector3;
import net.chromarenderer.math.Vector3;
import net.chromarenderer.math.geometry.Geometry;
import net.chromarenderer.math.geometry.PhotonFountain;
import net.chromarenderer.math.geometry.Sphere;
import net.chromarenderer.renderer.shader.Material;
import net.chromarenderer.renderer.shader.MaterialType;
import net.chromarenderer.renderer.ChromaRenderMode;
import net.chromarenderer.renderer.scene.GeometryScene;
import net.chromarenderer.renderer.scene.SceneFactory;
import net.chromarenderer.renderer.scene.acc.AccStructType;
import net.chromarenderer.utils.BufferPressedKeysEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChromaFxMain extends Application {

    //-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -XX:+LogCompilation

    private static final Chroma chroma = new Chroma();

    private static ChromaSettings settings;
    private static GeometryScene scene;

    private Stage previewStage;
    private Stage statisticsStage;


    @Override
    public void start(final Stage chromaMainStage) throws Exception {

        BorderPane mainBox = new BorderPane();
        GridPane controlPane = new GridPane();
        controlPane.setHgap(10);
        controlPane.setVgap(10);
        controlPane.setPadding(new Insets(0, 10, 0, 10));

        final boolean[] recreatePreview = {false};
        int rowIdx = 0;

        controlPane.add(new Text("Acc Struct:"), 0, rowIdx);
        ComboBox<AccStructType> accStructCombo = new ComboBox<>(FXCollections.observableArrayList(
                AccStructType.LIST,
                AccStructType.AABB_BVH
        ));
        controlPane.add(accStructCombo, 1, rowIdx++);
        accStructCombo.setValue(settings.getAccStructType());

        controlPane.add(new Text("Render Mode:"), 0, rowIdx);
        ComboBox<ChromaRenderMode> renderModeCombo = new ComboBox<>(FXCollections.observableArrayList(
                ChromaRenderMode.SIMPLE,
                ChromaRenderMode.PTDL,
                ChromaRenderMode.COLOR_CUBE,
                ChromaRenderMode.AVG
        ));
        controlPane.add(renderModeCombo, 1, rowIdx++);
        renderModeCombo.setValue(settings.getRenderMode());

        controlPane.add(new Text("Resolution:"), 0, rowIdx);
        ComboBox<String> resolutionCombo = new ComboBox<>(FXCollections.observableArrayList(
                "256x256", "512x512", "768x768", "1024x1024"
        ));
        controlPane.add(resolutionCombo, 1, rowIdx++);
        resolutionCombo.setValue(settings.getImgWidth() + "x" + settings.getImgHeight());
        resolutionCombo.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                recreatePreview[0] = true;
            }
        });


        controlPane.add(new Text("Multi Threading:"), 0, rowIdx);
        CheckBox parallelize = new CheckBox();
        controlPane.add(parallelize, 1, rowIdx++);
        parallelize.selectedProperty().setValue(settings.isMultiThreaded());

        controlPane.add(new Text("DL Estimation:"), 0, rowIdx);
        CheckBox directLightEstimation = new CheckBox();
        controlPane.add(directLightEstimation, 1, rowIdx++);
        directLightEstimation.selectedProperty().setValue(settings.isDirectLightEstimationEnabled());

        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(5));
        Button start = new Button("Start");
        start.setOnAction(event -> chroma.start());
        Button applySettings = new Button("Apply Settings");
        applySettings.setOnAction(event -> {
            start.setDisable(true);
            String[] split = resolutionCombo.getValue().split("x");
            settings = new ChromaSettings(
                    parallelize.selectedProperty().getValue(),
                    Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    renderModeCombo.getValue(),
                    directLightEstimation.selectedProperty().getValue(),
                    accStructCombo.getValue()
                    );
            chroma.reinit(settings, scene);
            if (recreatePreview[0]) {
                previewStage.close();
                previewStage = ChromaFxPreviewWindowFactory.createPreviewWindow(chroma);
                previewStage.initOwner(chromaMainStage);
                previewStage.show();
                recreatePreview[0] = false;
            }
            start.setDisable(false);
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
        Scene scene = new Scene(mainBox, 400, 400);
        chromaMainStage.setScene(scene);
        BufferPressedKeysEventHandler bufferPressedKeysEventHandler = new BufferPressedKeysEventHandler();
        chromaMainStage.addEventHandler(KeyEvent.ANY, bufferPressedKeysEventHandler);
        chromaMainStage.addEventHandler(KeyEvent.KEY_RELEASED, getKeyTypedEventHandler());

        previewStage = ChromaFxPreviewWindowFactory.createPreviewWindow(chroma);
        statisticsStage = ChromaFxStatusWindowFactory.createStatusWindow(chroma);
        previewStage.initOwner(chromaMainStage);
        statisticsStage.initOwner(chromaMainStage);

        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        final Menu windows = new Menu("Windows");
        final Menu menu3 = new Menu("Help");
        mainBox.setTop(menuBar);

        menuBar.getMenus().addAll(windows, menu3);
        MenuItem showPreview = new MenuItem("Preview");
        MenuItem showStatistics = new MenuItem("Statistics");
        windows.getItems().add(showPreview);
        windows.getItems().add(showStatistics);
        showPreview.setOnAction(event -> previewStage.show());
        showStatistics.setOnAction(event -> statisticsStage.show());

        chromaMainStage.setOnCloseRequest((arg0 -> {
            arg0.consume();
            statisticsStage.close();
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
        statisticsStage.show();
    }


    private Vector3 getRotationVector(Set<KeyCode> pressedKeys) {
        float rotX = 0.0f;
        float rotY = 0.0f;
        //float rotZ = 0.0f;

        boolean flushImage = false;

        for (KeyCode pressedKey : pressedKeys) {
            switch (pressedKey) {
                case NUMPAD7:
                    rotY -= 0.02f;
                    flushImage = true;
                    break;
                case NUMPAD9:
                    rotY += 0.02f;
                    flushImage = true;
                    break;
                case NUMPAD1:
                    rotX -= 0.02f;
                    flushImage = true;
                    break;
                case NUMPAD3:
                    rotX += 0.02f;
                    flushImage = true;
                    break;
            }

            if (flushImage) {
                chroma.flushOnNextImage();
            }
        }

        return new ImmutableVector3(rotX, rotY, 0.0f);
    }


    private Vector3 getTranslationVector(Set<KeyCode> pressedKeys) {

        float moveX = 0.0f;
        float moveY = 0.0f;
        float moveZ = 0.0f;
        boolean flushImage = false;


        // camera along negative z axis!
        for (KeyCode pressedKey : pressedKeys) {
            switch (pressedKey) {
                case NUMPAD4:
                case A:
                    moveX -= 0.1f;
                    flushImage = true;
                    break;
                case NUMPAD6:
                case D:
                    moveX += 0.1f;
                    flushImage = true;
                    break;
                case NUMPAD8:
                case W:
                    moveZ -= 0.1f;
                    flushImage = true;
                    break;
                case NUMPAD2:
                case S:
                    moveZ += 0.1f;
                    flushImage = true;
                    break;
                case PAGE_DOWN:
                case Q:
                    moveY -= 0.1f;
                    flushImage = true;
                    break;
                case PAGE_UP:
                case E:
                    moveY += 0.1f;
                    flushImage = true;
                    break;
            }

            if (flushImage) {
                chroma.flushOnNextImage();
            }
        }

        return new ImmutableVector3(moveX, moveY, moveZ);
    }


    private EventHandler<KeyEvent> getKeyTypedEventHandler() {
        return event -> {
            switch (event.getCode()) {
                case R:
                    chroma.getCamera().resetToInitial();
                    chroma.flushOnNextImage();
                    break;
            }
        };
    }


    private static List<Geometry> createSomeSpheres() {
        List<Geometry> result = new ArrayList<>();
        result.add(new Sphere(new ImmutableVector3(0.0f, -0.3f, 0.0f), 0.1, new Material(MaterialType.DIFFUSE, COLORS.BLUE)));
        result.add(new Sphere(new ImmutableVector3(-1.0f, 1.0f, -1.0f), 0.2, new Material(MaterialType.DIFFUSE, COLORS.RED)));
        result.add(new Sphere(new ImmutableVector3(1.0f, -0.4f, 1.0f), 0.2, new Material(MaterialType.DIFFUSE, COLORS.PURPLE)));
        result.add(new Sphere(new ImmutableVector3(-1.0f, 1.7f, -1.0f), 0.2, new Material(MaterialType.DIFFUSE, COLORS.GREEN)));
        result.add(new Sphere(new ImmutableVector3(1.0f, -1.5f, -1.0f), 0.4, new Material(MaterialType.MIRROR, COLORS.WHITE)));
        //result.add(new Sphere(new ImmutableVector3(0.0f, 0.0f, 0.0f), 0.125, new Material(MaterialType.EMITTING, new ImmutableVector3(20, 20 ,200))));
        result.add(new PhotonFountain(new ImmutableVector3(0.f, 0.f, 0.f), 2000.f));
        return result;
    }


    public static void main(String[] args) {
        Thread thread = new Thread(chroma);
        scene = SceneFactory.cornellBox(new ImmutableVector3(0, 0, 0), 2, createSomeSpheres());
        settings = new ChromaSettings(false, 512, 512, ChromaRenderMode.PTDL, true, AccStructType.AABB_BVH);
        chroma.reinit(settings, scene);
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
