package controllers;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import component.createTableColumn.CreateTableColumn;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class CreateTableController extends AnchorPane implements ControlledStage, Initializable {
    public JFXTextField tableNameField;
    public JFXListView<CreateTableColumn> list;
    private StageController stageController;
    private String databaseName;
    private MainWindowController controller;

    public CreateTableController(MainWindowController controller) {
        this.controller = controller;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(CREATE_TABLE_RES));
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
        if (result.code != Result.SUCCESS) {
            //TODO:
        }
//        stageController.setStage(MAIN_WINDOW, CREATE_TABLE);
//        ((MainWindowController)stageController.getController(MAIN_WINDOW_RES)).updateDatabases();
        controller.splitPane.getItems().remove(1);
        controller.updateDatabases();
    }

    public void cancel(ActionEvent actionEvent) {
//        stageController.setStage(MAIN_WINDOW, CREATE_TABLE);
        controller.splitPane.getItems().remove(1);
    }

    public void add(ActionEvent actionEvent) {
        list.getItems().add(new CreateTableColumn());
    }

    public void remove(ActionEvent actionEvent) {
        CreateTableColumn createTableColumn = list.getSelectionModel().getSelectedItem();
        list.getItems().remove(createTableColumn);
    }
}
