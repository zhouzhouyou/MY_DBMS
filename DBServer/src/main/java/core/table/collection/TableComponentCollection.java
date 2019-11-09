package core.table.collection;

import util.file.Block;
import util.file.BlockCollection;
import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;

public abstract class TableComponentCollection<T extends Block> extends BlockCollection<T> {
    public TableComponentCollection(String relativePath, String prefix, String postfix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, postfix);
    }

    public TableComponentCollection(String absolutePath) {
        super(absolutePath);
    }
}
