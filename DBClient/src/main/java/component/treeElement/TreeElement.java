package component.treeElement;

public class TreeElement {
    public enum Type {
        ROOT,
        DB,
        TABLE
    }

    public String name;
    public Type type;

    public TreeElement(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}
