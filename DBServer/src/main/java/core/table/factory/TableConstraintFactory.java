package core.table.factory;


import core.table.block.ConstraintBlock;
import core.table.block.TableBlock;
import core.table.collection.TableConstraintCollection;
import core.table.collection.TableDefineCollection;
import util.file.RandomAccessFiles;
import util.pair.Pair;
import util.result.Result;
import util.result.ResultFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableConstraintFactory extends TableComponentFactory<ConstraintBlock, TableConstraintCollection> {
    private TableDefineFactory defineFactory;
    private RandomAccessFiles raf;

    public TableConstraintFactory(TableBlock tableBlock) {
        super(tableBlock);
        defineFactory = tableBlock.getDefineFactory();
        raf = tableBlock.getRaf();
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
        TableDefineCollection defineCollection = defineFactory.getCollection();
        List<String> fieldNames = defineCollection.getFieldNames();
        Map<String, Object> recordMap = Pair.buildMap(fieldNames, record);
        Result result = collection.check(recordMap, raf);
        if (result.code != ResultFactory.SUCCESS) return result;
        record.clear();
        record.addAll(Pair.fromMap(recordMap));
        return result;
    }
}
