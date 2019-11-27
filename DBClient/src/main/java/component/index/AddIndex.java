package component.index;

import com.jfoenix.controls.JFXTextField;
import controllers.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
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

public class AddIndex extends AnchorPane implements Initializable {
    public JFXTextField nameField;
    public JFXTextField fieldField;
    public CheckBox ascField;
    public CheckBox uniqueField;

    private MainWindowController controller;
    private String databaseName;
    private String tableName;

    public AddIndex(MainWindowController controller) {
        this.controller = controller;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ADD_INDEX_RES));
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
        Bundle bundle = Bundle.INSTANCE;
        databaseName = bundle.getString(DATABASE);
        tableName = bundle.getString(TABLE);
        ascField.setSelected(true);
    }

    public void confirm() {
        String name = nameField.getText();
        String field = fieldField.getText();
        String temp = "";
        if (!ascField.isSelected()) temp += " desc";
        if (uniqueField.isSelected()) temp += " unique";
        String sql = String.format("create index %s on %s (%s)%s", name, tableName, field, temp);
        Result result = ClientHolder.INSTANCE.getClient().getResult(sql, databaseName);
        controller.clearSplitPane();
        if (result.code != Result.SUCCESS) {
            controller.showAlert(result);
            return;
        }
        controller.loadTable(tableName, databaseName);
    }

    public void cancel() {
        controller.clearSplitPane();
    }
}
