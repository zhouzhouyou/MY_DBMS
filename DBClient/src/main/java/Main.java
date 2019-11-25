import controllers.MainWindowController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.client.Client;
import util.stage.StageController;

import java.io.IOException;

import static util.Constant.SELECT_DATA_CENTER;
import static util.Constant.SELECT_DATA_CENTER_RES;

public class Main extends Application {
    private StageController stageController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stageController = new StageController();

        stageController.setPrimaryStage("primaryStage", primaryStage);
        stageController.loadStage(SELECT_DATA_CENTER, SELECT_DATA_CENTER_RES);
        stageController.setStage(SELECT_DATA_CENTER);
//        Parent root = MainWindowController.loader.load();
//        primaryStage.setTitle("title");
//        primaryStage.setScene(new Scene(root, 1200, 800));
//        primaryStage.show();
    }
}
