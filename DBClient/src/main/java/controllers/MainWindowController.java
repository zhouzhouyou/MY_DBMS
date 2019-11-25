package controllers;

import component.AbstractTreeItem;
import component.DatabaseTreeItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import util.client.Client;
import util.client.ClientHolder;
import util.result.Result;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static util.Constant.MAIN_WINDOW_RES;

@SuppressWarnings("unchecked")
public class MainWindowController implements Initializable, ControlledStage {
    private StageController stageController;
    private Client client;

    public BorderPane pane;
    public TreeView<String> treeView;
    public TableView tableView;
    public TextArea cmd;

    public void initialize(URL location, ResourceBundle resources) {
        client = ClientHolder.INSTANCE.getClient();
        initDatabases();
    }

    private void initDatabases() {
        Result result = client.getResult("get databases");
        List<String> databaseNames = (List<String>) result.data;

        TreeItem<String> rootItem = new TreeItem<>("root");
        rootItem.setExpanded(true);

        databaseNames.forEach(s -> {
            AbstractTreeItem treeItem = new DatabaseTreeItem(s);
            rootItem.getChildren().add(treeItem);
        });
        treeView.setRoot(rootItem);
        treeView.setCellFactory(param -> new DatabaseTreeCellImpl());
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

    private static final class DatabaseTreeCellImpl extends TreeCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getItem() == null ? "" : getItem());
                setContextMenu(((AbstractTreeItem)getTreeItem()).getMenu());
            }
        }


    }
}
