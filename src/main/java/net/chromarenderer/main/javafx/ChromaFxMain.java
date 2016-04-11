package net.chromarenderer.main.javafx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
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
    public void start(final Stage primaryStage) throws Exception {

        Pane root = new Pane();
        Scene scene = new Scene(root, settings.getImgWidth(), settings.getImgHeight());
        primaryStage.setScene(scene);
        primaryStage.setX(0);
        primaryStage.setX(0);
        primaryStage.setTitle("Chroma");

        BufferPressedKeysEventHandler bufferPressedKeysEventHandler = new BufferPressedKeysEventHandler();
        primaryStage.addEventHandler(KeyEvent.ANY, bufferPressedKeysEventHandler);
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, getKeyTypedEventHandler());

        previewStage = ChromaFxPreviewWindowFactory.createPreviewWindow(chroma);
        utilityStage = ChromaFxStatusWindowFactory.createStatusWindow(chroma);

        primaryStage.setOnCloseRequest((arg0 -> {
            arg0.consume();
            utilityStage.close();
            previewStage.close();
            primaryStage.close();
        }));

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                Set<KeyCode> pressedKeys = bufferPressedKeysEventHandler.getPressedKeys();
                chroma.moveCamera(getTranslationVector(pressedKeys), getRotationVector(pressedKeys));
            }
        }.start();

        primaryStage.toFront();
        primaryStage.show();
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


    private EventHandler<KeyEvent> getKeyTypedEventHandler() {
        return event -> {
            boolean reinitNeeded = false;

            switch (event.getCode()) {
                case F12:
                    chroma.takeScreenShot();
                    break;
                case C:
                    settings = settings.changeContinuousRender(!settings.isForceContinuousRender());
                    reinitNeeded = true;
                    break;
                case F5:
                    settings = settings.changeMode(ChromaRenderMode.AVG);
                    reinitNeeded = true;
                    break;
                case F6:
                    settings = settings.changeMode(ChromaRenderMode.COLOR_CUBE);
                    reinitNeeded = true;
                    break;
                case F7:
                    settings = settings.changeMode(ChromaRenderMode.SIMPLE);
                    reinitNeeded = true;
                    break;
                case F8:
                    settings = settings.changeMode(ChromaRenderMode.PTDL);
                    reinitNeeded = true;
                    break;
                case L:
                    settings = settings.toggleLightSourceSamplingMode();
                    reinitNeeded = true;
                    break;
                case ENTER:
                    chroma.restart();
                    break;
            }

            if (reinitNeeded) {
                chroma.init(settings);
                reinitNeeded = false;
            }
        };
    }


    public static void main(String[] args) {
        Thread thread = new Thread(chroma);
        settings = new ChromaSettings(512, 512, ChromaRenderMode.PTDL, true, 2, true, AccStructType.LIST);
        chroma.init(settings);

        thread.start();

        launch(ChromaFxMain.class, args);
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Bye");
    }

}
