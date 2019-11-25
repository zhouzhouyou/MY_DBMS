package component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class DatabaseTreeItem extends AbstractTreeItem {
    public DatabaseTreeItem(String name) {
        setValue(name);
    }

    @Override
    public ContextMenu getMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addDatabase = new MenuItem("增加数据库");
        addDatabase.setOnAction(event -> {

        });
        contextMenu.getItems().add(addDatabase);
        return contextMenu;
    }
}
