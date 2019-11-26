package component.constraint;

import com.jfoenix.controls.JFXTextField;
import controllers.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import util.Bundle;
import util.SQL;
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

public class AddConstraint extends AnchorPane implements Initializable, ControlledStage {
    public JFXTextField nameField;
    public JFXTextField fieldField;
    public JFXTextField paramField;
    public ChoiceBox<String> typeField;

    private StageController stageController;
    private MainWindowController controller;
    private String databaseName;
    private String tableName;
    private Bundle bundle;

    public AddConstraint(MainWindowController controller) {
        this.controller = controller;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ADD_CONSTRAINT_RES));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = Bundle.INSTANCE;
        databaseName = bundle.getString(DATABASE);
        tableName = bundle.getString(TABLE);
        typeField.getItems().addAll(SQL.PRIMARY_KEY, SQL.CHECK, SQL.NOT_NULL, SQL.UNIQUE, SQL.DEFAULT);
    }

    public void confirm() {
        String name = nameField.getText();
        String type = typeField.getValue();
        String temp = fieldField.getText() + " " + type;
        if (type.equals(SQL.CHECK)) temp = "check " + paramField.getText();
        if (type.equals(SQL.DEFAULT)) temp += " " + paramField.getText();
        String sql = String.format("alter table %s add constraint %s %s", tableName, name, temp);
        Result result = ClientHolder.INSTANCE.getClient().getResult(sql, databaseName);
        if (result.code != Result.SUCCESS) {
            //TODO:
        }
//        stageController.setStage(MAIN_WINDOW, ADD_CONSTRAINT);
        controller.splitPane.getItems().remove(1);
        controller.loadTable(tableName, databaseName);
    }

    public void cancel() {
//        stageController.setStage(MAIN_WINDOW, ADD_CONSTRAINT);
        controller.splitPane.getItems().remove(1);
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }
}
