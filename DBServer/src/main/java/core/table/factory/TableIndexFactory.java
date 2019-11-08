package core.table.factory;


import core.table.block.IndexBlock;
import core.table.block.TableBlock;
import core.table.collection.TableIndexCollection;

public class TableIndexFactory extends TableComponentFactory<IndexBlock, TableIndexCollection> {


    public TableIndexFactory(TableBlock tableBlock) {
        super(tableBlock);
    }

    @Override
    protected TableIndexCollection getInstance(TableBlock tableBlock) {
        return new TableIndexCollection(tableBlock.indexPath);
    }

    @Override
    protected String getAbsolutePath(TableBlock tableBlock) {
        return tableBlock.indexPath;
    }
}
