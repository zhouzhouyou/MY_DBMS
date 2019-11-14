package core.table.factory;


import core.table.block.ConstraintBlock;
import core.table.block.TableBlock;
import core.table.collection.TableConstraintCollection;
import core.table.collection.TableDefineCollection;
import util.result.Result;
import util.result.ResultFactory;

import java.util.List;

public class TableConstraintFactory extends TableComponentFactory<ConstraintBlock, TableConstraintCollection> {
    private TableDefineFactory defineFactory;

    public TableConstraintFactory(TableBlock tableBlock) {
        super(tableBlock);
        defineFactory = tableBlock.getDefineFactory();
    }

    @Override
    protected TableConstraintCollection getInstance(TableBlock tableBlock) {
        return new TableConstraintCollection(tableBlock.constraintPath);
    }

    @Override
    protected String getAbsolutePath(TableBlock tableBlock) {
        return tableBlock.constraintPath;
    }

    /**
     * 判断记录是否满足条件
     *
     * @param record 记录
     * @return 是否满足条件
     */
    public Result check(List<Object> record) {
        //TODO: 判断是否满足条件
        TableDefineCollection defineCollection = defineFactory.getCollection();
        List<String> fieldNames = defineCollection.getFieldNames();
        //TODO: for 循环来判断
        return ResultFactory.buildSuccessResult(null);
    }
}
