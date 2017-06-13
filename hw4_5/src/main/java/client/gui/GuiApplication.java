package client.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * Graphical interface for client to make requests to the server.
 */
public class GuiApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(@NotNull Stage primaryStage) {
        primaryStage.setTitle("FTP client");
        primaryStage.setScene(new SceneBuilder(primaryStage).getScene());
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}