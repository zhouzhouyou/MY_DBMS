package core.table.factory;


import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.collection.TableDefineCollection;

import java.util.List;

public class TableDefineFactory extends TableComponentFactory<DefineBlock, TableDefineCollection> {
    public TableDefineFactory(TableBlock tableBlock) {
        super(tableBlock);
    }

    @Override
    protected TableDefineCollection getInstance(TableBlock tableBlock) {
        return new TableDefineCollection(tableBlock.definePath);
    }

    @Override
    protected String getAbsolutePath(TableBlock tableBlock) {
        return tableBlock.definePath;
    }
}
