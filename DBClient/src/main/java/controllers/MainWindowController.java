package controllers;

import component.constraint.AddConstraint;
import component.field.AddField;
import component.menu.AbstractContextMenu;
import component.menu.DatabaseContextMenu;
import component.menu.RootContextMenu;
import component.menu.TableContextMenu;
import component.treeElement.DatabaseElement;
import component.treeElement.TableElement;
import component.treeElement.TreeElement;
import entity.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import util.table.TableHelper;

import java.net.URL;
import java.util.*;

import static util.Constant.*;
import static util.SQL.DATABASE;
import static util.SQL.TABLE;

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
    public SplitPane splitPane;

    private StageController stageController;
    private Client client;

    public BorderPane pane;
    public TreeView<TreeElement> treeView;

    private Bundle bundle;

    private String databaseName;
    private String tableName;

    public MainWindowController() {
    }

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

            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                System.out.println("single click");
                if (node instanceof Text || node instanceof TreeCell && ((TreeCell) node).getText() != null) {
                    TreeElement treeElement = treeView.getSelectionModel().getSelectedItem().getValue();
                    tableName = treeElement.name;
                    databaseName = ((TableElement) treeElement).db;
                    bundle.put(DATABASE, databaseName);
                    bundle.put(TABLE, tableName);
                    if (treeElement.type == TreeElement.Type.TABLE) {
                        loadTable(tableName, databaseName);
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

    public void loadTable(String tableName, String db) {
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
        Result selectResult = client.getResult("select * from " + tableName, db);
        if (selectResult.code == Result.SUCCESS) {
            Map<String, List<Object>> recordMap = (Map<String, List<Object>>) selectResult.data;
            loadData(recordMap);
        }
    }

    private void loadData(Map<String, List<Object>> recordMap) {
        List<String> columns = TableHelper.getColumns(recordMap);
        List<List<String>> rowValue = TableHelper.getColumnCells(recordMap);

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
            String constraintType = String.valueOf(list.get(2));
            Object param = list.get(3);
            constraintPropertyObservableList.add(new Constraint(constraintName, fieldName,constraintType, param).constraintProperty());
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
        treeView.getSelectionModel().clearSelection();
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
        CreateTableController createTableController = new CreateTableController(this);
        splitPane.getItems().add(createTableController);
//        stageController.loadStage(CREATE_TABLE, CREATE_TABLE_RES);
//        stageController.setStage(CREATE_TABLE);
    }

    public void deleteDB(String databaseName) {
        Result result = client.getResult("drop database " + databaseName);
        if (result.code != Result.SUCCESS) {
            //TODO:
        }
        treeView.getRoot().getChildren().removeIf(a -> a.getValue().name.equals(databaseName) && a.getValue().type.equals(TreeElement.Type.DB));
    }

    public void openTable(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        loadTable(tableName, databaseName);
    }

    public void deleteTable(String databaseName, String tableName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认信息");
        alert.setHeaderText(null);
        alert.setContentText("确认要删除表吗!");
        Optional<ButtonType> result=alert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            Result dropTable = client.getResult("drop table " + tableName, databaseName);
            updateDatabases();
        }
    }

    public void addDB() {
        TextInputDialog dialog = new TextInputDialog("newDataBase");
        dialog.setTitle("创建库");
        dialog.setHeaderText(null);
        dialog.setContentText("请输入库名");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()){
            String dbName=result.get();
            Result addDB = client.getResult("create database " + dbName);
            if (addDB.code == Result.SUCCESS) {
                treeView.getRoot().getChildren().add(new TreeItem<>(new DatabaseElement(dbName)));
            }
        }
    }

    public void addColumn(ActionEvent actionEvent) {
        splitPane.getItems().add(new AddField(this));
//        stageController.loadStage(ADD_FIELD, ADD_FIELD_RES);
//        stageController.setStage(ADD_FIELD, MAIN_WINDOW);
    }

    public void deleteColumn() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("删除列");
        dialog.setHeaderText(null);
        dialog.setContentText("请输入列名");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String columnName = result.get();
            Result dropColumn = client.getResult("alter table " + tableName + " drop column " + columnName, databaseName);
            loadTable(tableName, databaseName);
        }
    }

    public void deleteRecord() {
    }

    public void selectRecord() {

    }

    public void addConstraint() {
        splitPane.getItems().add(new AddConstraint(this));
//        stageController.loadStage(ADD_CONSTRAINT, ADD_CONSTRAINT_RES);
//        stageController.setStage(ADD_CONSTRAINT, MAIN_WINDOW);
    }

    public void deleteConstraint() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("删除索引");
        dialog.setHeaderText(null);
        dialog.setContentText("请输入索引名");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String constraintName = result.get();
            Result dropColumn = client.getResult("alter table " + tableName + " drop constraint " + constraintName, databaseName);
            loadTable(tableName, databaseName);
        }
    }

    public void updateDatabases() {
        Platform.runLater(this::initDatabases);
    }
}
