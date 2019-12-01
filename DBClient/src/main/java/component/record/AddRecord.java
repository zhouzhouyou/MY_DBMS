package component.record;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import controllers.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import util.client.ClientHolder;
import util.result.Result;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static util.Constant.ADD_RECORD_RES;

public class AddRecord extends AnchorPane implements Initializable {
    private List<String> columns;
    private MainWindowController controller;
    public JFXListView view;
    private List<JFXTextField> list = new ArrayList<>();
    public AddRecord(MainWindowController controller, List<String> columns) {
        this.columns = columns;
        this.controller = controller;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ADD_RECORD_RES));
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
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        for (int i = 0; i < columns.size(); i++) {
            Text column = new Text(columns.get(i));
            JFXTextField textField = new JFXTextField();
            list.add(textField);
            grid.add(column, i, 0);
            grid.add(textField, i, 1);
        }

        view.getItems().add(grid);

    }

    public void confirm() {
        StringBuilder fieldSB = new StringBuilder();
        StringBuilder valueSB = new StringBuilder();
        fieldSB.append("insert into ").append(controller.tableName).append(" (");
        for (int i = 0; i < columns.size() - 1; i++) {
            fieldSB.append(columns.get(i)).append(",");
            String value = list.get(i).getText();
            if (value == null || value.length() == 0) valueSB.append("null,");
            else valueSB.append(value).append(",");
        }
        String value = list.get(list.size()-1).getText();
        valueSB.append((value == null || value.length() == 0) ? "null" : value);
        fieldSB.append(columns.get(columns.size()-1)).append(") values (").append(valueSB).append(")");

//        List<String> cols = new ArrayList<>();
//        List<String> values = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            JFXTextField field = list.get(i);
//            if (field.getText().length() != 0) {
//                cols.add(columns.get(i));
//                values.add(field.getText());
//            }
//        }
//        for (int i = 0; i < cols.size() - 1; i++) {
//            sb.append(cols.get(i)).append(",");
//        }
//        sb.append(cols.get(cols.size()-1)).append(") values (");
//        for (int i = 0; i < values.size() - 1; i++) {
//            sb.append(values.get(i)).append(",");
//        }
//        sb.append(values.get(values.size()-1)).append(")");
        Result result = ClientHolder.INSTANCE.getClient().getResult(fieldSB.toString(), controller.databaseName);
        controller.clearSplitPane();
        if (result.code != Result.SUCCESS) {
            controller.showAlert(result);
        }
        controller.loadTable(controller.tableName, controller.databaseName);
    }

    public void cancel() {
        controller.clearSplitPane();
    }
}
