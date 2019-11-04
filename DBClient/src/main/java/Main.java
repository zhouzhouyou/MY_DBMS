import controllers.MainWindowController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = MainWindowController.loader.load();
        primaryStage.setTitle("title");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }
}
