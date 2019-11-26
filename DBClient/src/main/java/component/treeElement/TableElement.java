package component.treeElement;

public class TableElement extends TreeElement {
    public String db;
    public TableElement(String name, String db) {
        super(name, Type.TABLE);
        this.db = db;
    }
}
