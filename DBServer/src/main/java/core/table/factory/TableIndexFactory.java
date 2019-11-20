package core.table.factory;


import core.table.block.IndexBlock;
import core.table.block.IxBlock;
import core.table.block.TableBlock;
import core.table.collection.TableDefineCollection;
import core.table.collection.TableIndexCollection;
import util.file.RandomAccessFiles;
import util.pair.Pair;
import util.parser.parsers.CreateIndexParser;
import util.parser.parsers.DropIndexParser;
import util.result.Result;
import util.result.ResultFactory;

import java.util.List;
import java.util.Map;

public class TableIndexFactory extends TableComponentFactory<IndexBlock, TableIndexCollection> {
    private RandomAccessFiles raf;
    private TableDefineFactory defineFactory;

    public TableIndexFactory(TableBlock tableBlock) {
        super(tableBlock);
        raf = tableBlock.getRaf();
        defineFactory = tableBlock.getDefineFactory();
    }

    @Override
    protected TableIndexCollection getInstance(TableBlock tableBlock) {
        return new TableIndexCollection(tableBlock.indexPath);
    }

    @Override
    protected String getAbsolutePath(TableBlock tableBlock) {
        return tableBlock.indexPath;
    }

    /**
     * 创建一个新的索引并且将现有数据写入
     *
     * @param parser 创建索引SQL解析器
     * @return 创建索引结果
     */
    public Result createIndex(CreateIndexParser parser) {
        String indexName = parser.getIndexName();
        if (exists(indexName)) return ResultFactory.buildObjectAlreadyExistsResult(indexName);

        String fieldName = parser.getFieldName();
        boolean fieldAsc = parser.getFieldAsc();
        boolean unique = parser.isUnique();
        String indexFilePath = tablePath + indexName + ".ix";
        IndexBlock indexBlock = new IndexBlock(indexName, unique, fieldAsc, fieldName, raf, indexFilePath);
        Result result = indexBlock.create();
        if (result.code == ResultFactory.SUCCESS) {
            add(indexName, indexBlock);
        }
        return result;
    }

    /**
     * 删除一个索引
     *
     * @param parser 删除索引SQL解析器
     * @return 删除索引结果
     */
    public Result dropIndex(DropIndexParser parser) {
        String indexName = parser.getIndexName();
        if (exists(indexName)) return ResultFactory.buildObjectAlreadyExistsResult(indexName);
        IndexBlock block = map.get(indexName);
        map.values().removeIf(indexBlock -> indexBlock.equals(block));
        collection.remove(block);
        return block.delete();
    }

    /**
     * 删除一组记录
     *
     * @param indexes 一组记录的索引
     * @return 删除结果
     */
    public Result deleteRecord(List<Integer> indexes) {
        return collection.deleteRecord(indexes);
    }

    /**
     * 删除一条记录
     *
     * @param index 记录的索引
     * @return 删除结果
     */
    public Result deleteRecord(int index) {
        return collection.deleteRecord(index);
    }

    /**
     * 试图插入一条记录进入索引，没有在索引中的域会被无视
     *
     * @param record 一条记录
     * @param index  记录的索引
     * @return 成功插入则返回 {@link ResultFactory#SUCCESS}
     */
    public Result insertRecord(List<Object> record, int index) {
        TableDefineCollection defineCollection = defineFactory.collection;
        List<String> fieldNames = defineCollection.getFieldNames();
        Map<String, Object> recordMap = Pair.buildMap(fieldNames, record);
        return collection.insertRecord(recordMap, index);
    }

    public IxBlock getRelativeIxBlock(String fieldName) {
        return collection.getRelativeIxBlock(fieldName);
    }
}
