package component.database;

import com.jfoenix.controls.JFXTextField;
import controllers.MainWindowController;
import javafx.fxml.Initializable;
import util.Bundle;
import util.client.ClientHolder;
import util.result.Result;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.net.URL;
import java.util.ResourceBundle;

import static util.Constant.*;
import static util.SQL.DATABASE;

public class AddDatabase implements ControlledStage, Initializable {
    public JFXTextField databaseNameField;
    private StageController stageController;
    private Bundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseNameField.setText("newDatabase");
        bundle = Bundle.INSTANCE;
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }

    public void confirm() {
        Result result = ClientHolder.INSTANCE.getClient().getResult("create database " + databaseNameField.getText());
        if (result.code != Result.SUCCESS) {

        }
        bundle.put(DATABASE, databaseNameField.getText());


        stageController.setStage(MAIN_WINDOW, ADD_DATABASE);

        ((MainWindowController) stageController.getController(MAIN_WINDOW_RES)).updateDatabases();
    }

    public void cancel() {
        stageController.setStage(MAIN_WINDOW, ADD_DATABASE);
    }
}
