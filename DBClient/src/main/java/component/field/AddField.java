package component.field;

import com.jfoenix.controls.JFXTextField;
import controllers.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import util.Bundle;
import util.client.ClientHolder;
import util.result.Result;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static util.Constant.*;
import static util.SQL.DATABASE;
import static util.SQL.TABLE;

public class AddField extends AnchorPane implements Initializable, ControlledStage {
    public JFXTextField fieldNameField;
    public JFXTextField typeField;
    private String databaseName;
    private String tableName;
    private Bundle bundle;
    private StageController stageController;
    MainWindowController controller;

    public AddField(MainWindowController controller) {
        this.controller = controller;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ADD_FIELD_RES));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = Bundle.INSTANCE;
        databaseName = bundle.getString(DATABASE);
        tableName = bundle.getString(TABLE);
    }

    public void confirm() {
        String fieldName = fieldNameField.getText();
        String type = typeField.getText();
        String sql = String.format("alter table %s add column %s %s", tableName, fieldName, type);
        Result result = ClientHolder.INSTANCE.getClient().getResult(sql, databaseName);
        controller.clearSplitPane();
        if (result.code != Result.SUCCESS) {
            controller.showAlert(result);
            return;
        }

        controller.updateDatabases();
//        stageController.setStage(MAIN_WINDOW, ADD_FIELD);
    }

    public void cancel() {
//        stageController.setStage(MAIN_WINDOW, ADD_FIELD);
        controller.clearSplitPane();
    }
}
