package util.table;

import core.table.block.TableBlock;
import util.parser.parsers.CreateIndexParser;
import util.parser.parsers.DropIndexParser;
import util.result.Result;

public enum IndexUtil {
    INSTANCE;

    public Result createIndex(TableBlock tableBlock, CreateIndexParser parser) {
        String indexName = parser.getIndexName();
        String fieldName = parser.getFieldName();
        boolean fieldAsc = parser.getFieldAsc();
        boolean unique = parser.isUnique();
        //TODO: 创建一个IxBlock对象并且序列化到对应的位置
        return null;

    }

    public Result dropIndex(TableBlock tableBlock, DropIndexParser parser) {
        //TODO: 删除一个索引文件
        return null;
    }
}
