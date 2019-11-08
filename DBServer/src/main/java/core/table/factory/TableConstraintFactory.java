package core.table.factory;


import core.table.block.ConstraintBlock;
import core.table.block.TableBlock;
import core.table.collection.TableConstraintCollection;

public class TableConstraintFactory extends TableComponentFactory<ConstraintBlock, TableConstraintCollection> {


    public TableConstraintFactory(TableBlock tableBlock) {
        super(tableBlock);
    }

    @Override
    protected TableConstraintCollection getInstance(TableBlock tableBlock) {
        return new TableConstraintCollection(tableBlock.constraintPath);
    }

    @Override
    protected String getAbsolutePath(TableBlock tableBlock) {
        return tableBlock.constraintPath;
    }
}
