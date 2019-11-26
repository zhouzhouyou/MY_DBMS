package controllers;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import component.menu.AbstractContextMenu;
import component.menu.DatabaseContextMenu;
import component.menu.RootContextMenu;
import component.menu.TableContextMenu;
import component.treeElement.DatabaseElement;
import component.treeElement.TableElement;
import component.treeElement.TreeElement;
import entity.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.util.*;

import static util.Constant.*;
import static util.SQL.DATABASE;

@SuppressWarnings("unchecked")
public class MainWindowController implements Initializable, ControlledStage {
    public TableView<DefineProperty> tableDefineView;
    private final ObservableList<DefineProperty> definePropertyObservableList = FXCollections.observableArrayList();
    private final ObservableList<ConstraintProperty> constraintPropertyObservableList = FXCollections.observableArrayList();
    private final ObservableList<IndexProperty> indexPropertyObservableList = FXCollections.observableArrayList();
    public TableView tableDataView;
    public TableView tableConstraintView;
    public TableView tableIndexView;
    public TableColumn<DefineProperty, String> defineFieldColumn;
    public TableColumn<DefineProperty, String> defineTypeColumn;
    public TableColumn<DefineProperty, String> definePkColumn;
    public TableColumn<DefineProperty, String> defineUniqueColumn;
    public TableColumn<DefineProperty, String> defineNotNullColumn;
    public TableColumn<DefineProperty, String> defineCheckColumn;
    public TableColumn<DefineProperty, String> defineDefaultColumn;
    public TableColumn<ConstraintProperty, String> constraintNameColumn;
    public TableColumn<ConstraintProperty, String> constraintFieldColumn;
    public TableColumn<ConstraintProperty, String> constraintTypeColumn;
    public TableColumn<ConstraintProperty, String> constraintParamColumn;
    public TableColumn<IndexProperty, String> indexNameColumn;
    public TableColumn<IndexProperty, String> indexFieldColumn;
    public TableColumn<IndexProperty, String> indexAscColumn;
    public TableColumn<IndexProperty, String> indexUniqueColumn;

    private StageController stageController;
    private Client client;

    public BorderPane pane;
    public TreeView<TreeElement> treeView;
    public TextArea cmd;

    private DatabaseContextMenu databaseContextMenu;
    private TableContextMenu contextMenu;
    private RootContextMenu rootContextMenu;

    private Bundle bundle;
    private Boolean asc;

    public void initialize(URL location, ResourceBundle resources) {
        client = ClientHolder.INSTANCE.getClient();
        bundle = Bundle.INSTANCE;
        tableDataView.setItems(definePropertyObservableList);
        initDefineView();
        initConstraintView();
        initIndexView();
        initTreeView();
        initDatabases();
    }

    private void initDefineView() {
        defineFieldColumn.setCellValueFactory(cellData -> cellData.getValue().fieldNameProperty());
        defineTypeColumn.setCellValueFactory(cellData -> cellData.getValue().fieldTypeProperty());
        definePkColumn.setCellValueFactory(cellData -> cellData.getValue().pkProperty());
        defineUniqueColumn.setCellValueFactory(cellData -> cellData.getValue().uniqueProperty());
        defineNotNullColumn.setCellValueFactory(cellData -> cellData.getValue().notNullProperty());
        defineCheckColumn.setCellValueFactory(cellData -> cellData.getValue().checkProperty());
        defineDefaultColumn.setCellValueFactory(cellData -> cellData.getValue().defaultValueProperty());
        tableDefineView.setItems(definePropertyObservableList);

    }

    private void initConstraintView() {
        constraintNameColumn.setCellValueFactory(cellData -> cellData.getValue().constraintNameProperty());
        constraintFieldColumn.setCellValueFactory(cellData -> cellData.getValue().fieldNameProperty());
        constraintTypeColumn.setCellValueFactory(cellData -> cellData.getValue().constraintTypeProperty());
        constraintParamColumn.setCellValueFactory(cellData -> cellData.getValue().constraintParamProperty());
        tableConstraintView.setItems(constraintPropertyObservableList);
    }

    private void initIndexView() {
        indexNameColumn.setCellValueFactory(cellData -> cellData.getValue().indexNameProperty());
        indexFieldColumn.setCellValueFactory(cellData -> cellData.getValue().indexFieldProperty());
        indexAscColumn.setCellValueFactory(cellData -> cellData.getValue().indexAscProperty());
        indexUniqueColumn.setCellValueFactory(cellData -> cellData.getValue().indexUniqueProperty());
        tableIndexView.setItems(indexPropertyObservableList);
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
                            loadTable(treeElement.name, ((TableElement) treeElement).db);
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

    private void loadTable(String tableName, String db) {
        Result defineResult = client.getResult("get table_define " + tableName, db);
        if (defineResult.code == Result.SUCCESS) {
            List<List<Object>> define = (List<List<Object>>) defineResult.data;
            loadDefine(define);
        }
        Result constraintResult = client.getResult("get table_constraint " + tableName, db);
        if (constraintResult.code == Result.SUCCESS) {
            List<List<Object>> constraint = (List<List<Object>>) constraintResult.data;
            loadConstraint(constraint);
        }
        Result indexResult = client.getResult("get table_index " + tableName, db);
        if (indexResult.code == Result.SUCCESS) {
            List<List<Object>> index = (List<List<Object>>) indexResult.data;
            loadIndex(index);
        }
    }

    private void loadDefine(List<List<Object>> data) {
        definePropertyObservableList.clear();
        for (List<Object> list : data) {
            String fieldName = (String) list.get(0);
            String fieldType = (String) list.get(1);
            Boolean pk = (Boolean) list.get(2);
            Boolean notNull = (Boolean) list.get(3);
            Boolean unique = (Boolean) list.get(4);
            String check = (String) list.get(5);
            Object defaultValue = list.get(6);
            definePropertyObservableList.add(new Define(fieldName, fieldType, pk, notNull, unique, check, defaultValue).defineProperty());
            //defineList.add(new Define(fieldName, fieldType, pk, notNull, unique, check, defaultValue));
        }
//        defineList.forEach(define -> definePropertyObservableList.add(define.defineProperty()));

    }

    private void loadConstraint(List<List<Object>> data) {
        constraintPropertyObservableList.clear();
        for (List<Object> list : data) {
            String constraintName = (String) list.get(0);
            String fieldName = (String) list.get(1);
            Integer constraintType = (Integer) list.get(2);
            Object param = list.get(3);
            constraintPropertyObservableList.add(new Constraint(constraintName, fieldName, constraintType, param).constraintProperty());
        }
    }

    private void loadIndex(List<List<Object>> data) {
        indexPropertyObservableList.clear();
        for (List<Object> list : data) {
            String indexName = (String) list.get(0);
            String field = (String) list.get(1);
            Boolean asc = (Boolean) list.get(2);
            Boolean unique = (Boolean) list.get(3);
            indexPropertyObservableList.add(new Index(indexName, unique, asc, field).indexProperty());
        }
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

    public void addColumn(ActionEvent actionEvent) {
    }

    public void deleteColumn(ActionEvent actionEvent) {
    }

    public void deleteRecord(ActionEvent actionEvent) {
    }

    public void selectRecord(ActionEvent actionEvent) {
    }

    public void addConstraint(ActionEvent actionEvent) {
    }

    public void deleteConstraint(ActionEvent actionEvent) {
    }
}
