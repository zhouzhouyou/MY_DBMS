package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import util.client.Client;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.net.URL;
import java.util.ResourceBundle;

import static util.Constant.MAIN_WINDOW_RES;

public class MainWindowController implements Initializable, ControlledStage {
    public static FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource(MAIN_WINDOW_RES));
    private StageController stageController;

    public BorderPane pane;
    public TreeView treeView;
    public TableView tableView;
    public TextArea cmd;

    public void initialize(URL location, ResourceBundle resources) {

    }

    private Stage getStage() {
        return (Stage) pane.getScene().getWindow();
    }

    public void closeDataCenter(ActionEvent actionEvent) {
        getStage().close();
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }
}
