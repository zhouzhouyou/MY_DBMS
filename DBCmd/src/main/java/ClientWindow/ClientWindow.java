package ClientWindow;

import ClientWindow.controllers.ClientWindowController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = ClientWindowController.loader.load();
        primaryStage.setTitle("title");
        primaryStage.setScene(new Scene(root, 650, 400));
        primaryStage.show();
    }
}
