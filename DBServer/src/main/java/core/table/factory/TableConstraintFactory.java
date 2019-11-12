package core.table.factory;


import core.table.block.ConstraintBlock;
import core.table.block.RecordBlock;
import core.table.block.TableBlock;
import core.table.collection.TableConstraintCollection;
import util.result.Result;
import util.result.ResultFactory;

import java.util.List;

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

    public Result check(List<Object> toInsert) {
        return ResultFactory.buildSuccessResult(null);
    }
}
