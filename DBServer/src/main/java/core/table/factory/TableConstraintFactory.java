package core.table.factory;


import core.table.block.ConstraintBlock;
import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.collection.TableConstraintCollection;
import core.table.collection.TableDefineCollection;
import util.file.RandomAccessFiles;
import util.pair.Pair;
import util.result.Result;
import util.result.ResultFactory;
import util.table.FieldTypes;

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
        List<Integer> fieldTypes = defineCollection.getFieldTypes();
        List<String> fieldNames = defineCollection.getFieldNames();
        List<Pair<String, Object>> recordMap = Pair.buildPairList(fieldNames, record);
        for (int i = 0; i < defineCollection.list.size(); i++) {
            DefineBlock defineBlock = defineCollection.list.get(i);
            if (defineBlock.fieldType == FieldTypes.VARCHAR) {
                String string = (String) recordMap.get(i).getLast();
                if (string.length() > defineBlock.param) return ResultFactory.buildFailResult(string);
            }
        }
        Result result = collection.check(recordMap, fieldTypes, raf);
        if (result.code != ResultFactory.SUCCESS) return result;
        record.clear();
        record.addAll(Pair.fromPairList(recordMap));
        return result;
    }
}
