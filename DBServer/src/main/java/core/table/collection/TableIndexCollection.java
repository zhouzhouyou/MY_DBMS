package core.table.collection;

import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;
import core.table.block.IndexBlock;

public class TableIndexCollection extends TableComponentCollection<IndexBlock> {
    public static final String TABLE_INDEX_POSTFIX = "tid";

    public TableIndexCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_INDEX_POSTFIX);
    }

    public TableIndexCollection(String absolutePath) {
        super(absolutePath);
    }
}
