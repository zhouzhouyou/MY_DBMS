package controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import component.createTableColumn.CreateTableColumn;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import util.Bundle;
import util.SQL;
import util.client.ClientHolder;
import util.result.Result;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.net.URL;
import java.util.ResourceBundle;

import static util.Constant.CREATE_TABLE;
import static util.Constant.MAIN_WINDOW;

public class CreateTableController implements ControlledStage, Initializable {
    public JFXTextField tableNameField;
    public JFXListView<CreateTableColumn> list;
    private StageController stageController;
    private String databaseName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseName = Bundle.INSTANCE.getString(SQL.DATABASE);
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }

    public void create(ActionEvent actionEvent) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table ").append(tableNameField.getText()).append(" (");
        ObservableList<CreateTableColumn> observableList = list.getItems();
        for (int i = 0; i < observableList.size() - 1; i++) {
            CreateTableColumn createTableColumn = list.getItems().get(i);
            sb.append(createTableColumn.getValue()).append(",");
        }
        sb.append(observableList.get(observableList.size()-1).getValue()).append(")");
        Bundle.INSTANCE.put(SQL.CREATE_TABLE, sb.toString());
        Result result =ClientHolder.INSTANCE.getClient().getResult(sb.toString(), databaseName);
        if (result.code == Result.SUCCESS) {
            //TODO:
        }
        stageController.setStage(MAIN_WINDOW, CREATE_TABLE);
    }

    public void cancel(ActionEvent actionEvent) {
    }

    public void add(ActionEvent actionEvent) {
        list.getItems().add(new CreateTableColumn());
    }

    public void remove(ActionEvent actionEvent) {
        CreateTableColumn createTableColumn = list.getSelectionModel().getSelectedItem();
        list.getItems().remove(createTableColumn);
    }
}
