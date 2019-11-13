package core.table.collection;

import util.file.Block;
import util.file.BlockCollection;
import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;

/**
 * 存储着数据库下所有的表的某个类型的组件
 *
 * @param <T> 某个具体类型的block
 */
public abstract class TableComponentCollection<T extends Block> extends BlockCollection<T> {
    public TableComponentCollection(String relativePath, String prefix, String postfix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, postfix);
    }

    public TableComponentCollection(String absolutePath) {
        super(absolutePath);
    }
}
