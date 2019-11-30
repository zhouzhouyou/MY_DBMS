package component.createTableColumn;


import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import util.SQL;

import java.io.IOException;

import static util.Constant.CREATE_TABLE_COLUMN_RES;

public class CreateTableColumn extends AnchorPane {
    public JFXTextField fieldNameField;
    public JFXTextField typeField;
    public JFXTextField defaultValueField;
    public CheckBox notNullBtn;
    public CheckBox uniqueBtn;
    public CheckBox pkBtn;
    public JFXTextField checkField;

    public CreateTableColumn() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(CREATE_TABLE_COLUMN_RES));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getValue() {
        StringBuilder sb = new StringBuilder();
        sb.append(fieldNameField.getText());
        sb.append(" ").append(typeField.getText());
        if (defaultValueField.getText().trim().length() != 0) sb.append(" default ").append(defaultValueField.getText());
        if (pkBtn.isSelected()) sb.append(" ").append(SQL.PRIMARY_KEY);
        if (uniqueBtn.isSelected()) sb.append(" ").append(SQL.UNIQUE);
        if (notNullBtn.isSelected()) sb.append(" ").append(SQL.NOT_NULL);
        if (checkField.getText().trim().length() != 0) sb.append(" check (").append(checkField.getText()).append(")");
        return sb.toString();
    }
}
