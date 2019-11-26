package component.index;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import util.Bundle;
import util.client.ClientHolder;
import util.result.Result;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.net.URL;
import java.util.ResourceBundle;

import static util.Constant.ADD_INDEX;
import static util.Constant.MAIN_WINDOW;
import static util.SQL.DATABASE;
import static util.SQL.TABLE;

public class AddIndex extends AnchorPane implements Initializable, ControlledStage {
    public JFXTextField nameField;
    public JFXTextField fieldField;
    public CheckBox ascField;
    public CheckBox uniqueField;

    private StageController stageController;

    private String databaseName;
    private String tableName;
    private Bundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = Bundle.INSTANCE;
        databaseName = bundle.getString(DATABASE);
        tableName = bundle.getString(TABLE);
        ascField.setSelected(true);
    }

    @Override
    public void setStageController(StageController stageController) {
        this.stageController = stageController;
    }

    public void confirm() {
        String name = nameField.getText();
        String field = fieldField.getText();
        String temp = "";
        if (!ascField.isSelected()) temp += " desc";
        if (uniqueField.isSelected()) temp += " unique";
        String sql = String.format("create index %s on %s (%s)%s", name, tableName, field, temp);
        Result result = ClientHolder.INSTANCE.getClient().getResult(sql, databaseName);
        if (result.code != Result.SUCCESS) {
            //TODO:
        }
        stageController.setStage(MAIN_WINDOW, ADD_INDEX);
    }

    public void cancel() {
        stageController.setStage(MAIN_WINDOW, ADD_INDEX);
    }
}
