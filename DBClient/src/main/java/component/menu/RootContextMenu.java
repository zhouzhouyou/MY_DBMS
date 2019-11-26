package component.menu;

import controllers.MainWindowController;
import javafx.scene.control.MenuItem;

public class RootContextMenu extends AbstractContextMenu {
    public RootContextMenu(MainWindowController controller) {
        MenuItem addDB = new MenuItem("增加数据库");
        addDB.setOnAction(event -> controller.addDB());
        getItems().addAll(addDB);
    }
}
