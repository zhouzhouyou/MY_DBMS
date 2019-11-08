package core.table;


import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;
import core.file.BlockCollection;


public class TableCollection extends BlockCollection<TableBlock> {
    public static final String TABLE_POSTFIX = "tb";

    public TableCollection(String path, String prefix) throws EmptyNameException, IllegalNameException {
        super(path, prefix, TABLE_POSTFIX);
    }

    public TableCollection(String absolutePath) {
        super(absolutePath);
    }
}
