package controllers;

import component.menu.AbstractContextMenu;
import component.menu.DatabaseContextMenu;
import component.menu.RootContextMenu;
import component.menu.TableContextMenu;
import component.treeElement.DatabaseElement;
import component.treeElement.TableElement;
import component.treeElement.TreeElement;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.Bundle;
import util.client.Client;
import util.client.ClientHolder;
import util.result.Result;
import util.stage.ControlledStage;
import util.stage.StageController;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static util.Constant.*;
import static util.SQL.DATABASE;

@SuppressWarnings("unchecked")
public class MainWindowController implements Initializable, ControlledStage {
    public TableView tableDefineView;
    public TableView tableDataView;
    public TableView tableConstraintView;
    public TableView tableIndexView;
    private StageController stageController;
    private Client client;

    public BorderPane pane;
    public TreeView<TreeElement> treeView;
    public TextArea cmd;

    private DatabaseContextMenu databaseContextMenu;
    private TableContextMenu contextMenu;
    private RootContextMenu rootContextMenu;

    private Bundle bundle;

    public void initialize(URL location, ResourceBundle resources) {
        client = ClientHolder.INSTANCE.getClient();
        bundle = Bundle.INSTANCE;
        initTreeView();
        initDatabases();
    }

    private void initTreeView() {
        treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Node node = event.getPickResult().getIntersectedNode();

            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                System.out.println("single click");
                if (node instanceof Text || node instanceof TreeCell && ((TreeCell) node).getText() != null) {
                    TreeElement treeElement = treeView.getSelectionModel().getSelectedItem().getValue();
                    switch (treeElement.type) {
                        case TABLE:
                            loadTable(treeElement.name);
                    }
                }
            } else if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
                System.out.println("right click");
                if (node instanceof Text || node instanceof TreeCell && ((TreeCell) node).getText() != null) {
                    TreeElement treeElement = treeView.getSelectionModel().getSelectedItem().getValue();
                    AbstractContextMenu contextMenu = null;
                    switch (treeElement.type) {
                        case ROOT:
                            contextMenu = RootContextMenu.setInstance(new RootContextMenu(this));
                            break;
                        case DB:
                            contextMenu = RootContextMenu.setInstance(new DatabaseContextMenu(treeElement.name, this));
                            break;
                        case TABLE:
                            String dbName = treeView.getSelectionModel().getSelectedItem().getParent().getValue().name;
                            contextMenu = RootContextMenu.setInstance(new TableContextMenu(dbName, treeElement.name, this));
                            break;
                    }
                    contextMenu.show(node, Side.RIGHT, 0, 0);
                }
            }
        });
    }

    private void loadTable(String tableName) {
        Result defineResult = client.getResult("get table_define " + tableName);
        if (defineResult.code == Result.SUCCESS) loadDefine((String)defineResult.data);
    }

    private void loadDefine(String data) {

    }

    private void initDatabases() {
        Result result = client.getResult("get databases");
        if (result.code != Result.SUCCESS) {
            //TODO:
        }

        List<String> databaseNames = (List<String>) result.data;
        List<DatabaseElement> databaseElements = new LinkedList<>();
        TreeItem<TreeElement> rootItem = new TreeItem<>(new TreeElement("数据中心", TreeElement.Type.ROOT));
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);

        databaseNames.forEach(s -> databaseElements.add(new DatabaseElement(s)));
        databaseElements.forEach(databaseElement -> rootItem.getChildren().add(new TreeItem<>(databaseElement)));

        List<TableElement> tableElements = new LinkedList<>();
        for (DatabaseElement databaseElement : databaseElements) {
            String dbName = databaseElement.name;
            Result getTableNameResult = client.getResult(String.format("get tables %s", dbName));
            if (getTableNameResult.code != Result.SUCCESS) {
                //TODO:
            }
            ((List<String>) getTableNameResult.data).forEach(s -> tableElements.add(new TableElement(s, dbName)));
        }

        tableElements.forEach(tableElement -> rootItem.getChildren().forEach(dbItem -> {
            if (dbItem.getValue().name.equals(tableElement.db)) dbItem.getChildren().add(new TreeItem<>(tableElement));
        }));
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

    public void createTable(String databaseName) {
        bundle.put(DATABASE, databaseName);
        stageController.loadStage(CREATE_TABLE, CREATE_TABLE_RES);
        stageController.setStage(CREATE_TABLE, MAIN_WINDOW);
    }

    public void deleteDB(String databaseName) {

    }

    public void openTable(String databaseName, String tableName) {
    }

    public void deleteTable(String databaseName, String tableName) {

    }

    public void addDB() {
    }
}
