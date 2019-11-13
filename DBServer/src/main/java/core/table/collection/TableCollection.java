package core.table.collection;


import core.table.block.TableBlock;
import util.file.BlockCollection;
import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;

/**
 * 存储着数据库下所有的表
 */
public class TableCollection extends BlockCollection<TableBlock> {
    public static final String TABLE_POSTFIX = "tb";

    public TableCollection(String path, String prefix) throws EmptyNameException, IllegalNameException {
        super(path, prefix, TABLE_POSTFIX);
    }

    public TableCollection(String absolutePath) {
        super(absolutePath);
    }
}
