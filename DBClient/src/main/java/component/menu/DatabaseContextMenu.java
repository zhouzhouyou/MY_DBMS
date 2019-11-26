package component.menu;

import controllers.MainWindowController;
import javafx.scene.control.MenuItem;

public class DatabaseContextMenu extends AbstractContextMenu {
    public DatabaseContextMenu(String databaseName, MainWindowController controller) {
        MenuItem createTable = new MenuItem("创建表");
        createTable.setOnAction(event -> controller.createTable(databaseName));
        MenuItem deleteDB = new MenuItem("删除数据库");
        deleteDB.setOnAction(event -> controller.deleteDB(databaseName));
        getItems().addAll(createTable, deleteDB);
    }
}
