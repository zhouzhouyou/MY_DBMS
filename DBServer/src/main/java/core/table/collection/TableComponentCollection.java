package core.table.collection;

import core.file.Block;
import core.file.BlockCollection;
import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;

public abstract class TableComponentCollection<T extends Block> extends BlockCollection<T> {
    public TableComponentCollection(String relativePath, String prefix, String postfix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, postfix);
    }

    public TableComponentCollection(String absolutePath) {
        super(absolutePath);
    }
}
