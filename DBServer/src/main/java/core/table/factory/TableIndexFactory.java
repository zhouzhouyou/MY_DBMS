package core.table.factory;


import core.table.block.IndexBlock;
import core.table.block.TableBlock;
import core.table.collection.TableIndexCollection;
import util.file.RandomAccessFiles;
import util.parser.parsers.CreateIndexParser;
import util.parser.parsers.DropIndexParser;
import util.result.Result;
import util.result.ResultFactory;

public class TableIndexFactory extends TableComponentFactory<IndexBlock, TableIndexCollection> {
    private RandomAccessFiles raf;

    public TableIndexFactory(TableBlock tableBlock) {
        super(tableBlock);
        raf = tableBlock.getRaf();
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
}
