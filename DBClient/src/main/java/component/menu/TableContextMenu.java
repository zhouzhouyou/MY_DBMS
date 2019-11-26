package component.menu;

import controllers.MainWindowController;
import javafx.scene.control.MenuItem;

public class TableContextMenu extends AbstractContextMenu {
    public TableContextMenu(String databaseName, String tableName, MainWindowController controller) {
        MenuItem openTable = new MenuItem("打开表");
        openTable.setOnAction(event -> controller.openTable(databaseName, tableName));
        MenuItem deleteTable = new MenuItem("删除表");
        deleteTable.setOnAction(event -> controller.deleteTable(databaseName, tableName));
        getItems().addAll(openTable, deleteTable);
    }
}
