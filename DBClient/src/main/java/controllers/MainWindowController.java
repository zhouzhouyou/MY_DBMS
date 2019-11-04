package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    public static FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("/MainWindow.fxml"));

    public BorderPane pane;

    public void initialize(URL location, ResourceBundle resources) {

    }


    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open whatever");
        fileChooser.showOpenDialog(getStage());
    }

    public void close(ActionEvent actionEvent) {
        getStage().close();
    }

    private Stage getStage() {
        return (Stage) pane.getScene().getWindow();
    }
}
