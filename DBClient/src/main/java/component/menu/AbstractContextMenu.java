package component.menu;

import javafx.scene.control.ContextMenu;

public class AbstractContextMenu extends ContextMenu {
    public static AbstractContextMenu INSTANCE;

    public static AbstractContextMenu setInstance(AbstractContextMenu abstractContextMenu) {
        INSTANCE = abstractContextMenu;
        return INSTANCE;
    }
}
