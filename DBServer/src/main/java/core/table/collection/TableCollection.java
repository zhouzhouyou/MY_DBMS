package core.table.collection;


import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;
import util.file.BlockCollection;
import core.table.block.TableBlock;


public class TableCollection extends BlockCollection<TableBlock> {
    public static final String TABLE_POSTFIX = "tb";

    public TableCollection(String path, String prefix) throws EmptyNameException, IllegalNameException {
        super(path, prefix, TABLE_POSTFIX);
    }

    public TableCollection(String absolutePath) {
        super(absolutePath);
    }
}
