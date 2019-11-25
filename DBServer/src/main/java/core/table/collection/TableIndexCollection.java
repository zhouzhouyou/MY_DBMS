package core.table.collection;

import core.table.block.IndexBlock;
import core.table.block.IxBlock;
import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;
import util.result.Result;
import util.result.ResultFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表的所有索引
 */
public class TableIndexCollection extends TableComponentCollection<IndexBlock> {
    public static final String TABLE_INDEX_POSTFIX = "tid";

    public TableIndexCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_INDEX_POSTFIX);
    }

    public TableIndexCollection(String absolutePath) {
        super(absolutePath);
    }

    /**
     * 删除一条记录
     *
     * @param index 记录的索引
     * @return 删除结果
     */
    public Result deleteRecord(int index) {
        return deleteRecord(Collections.singletonList(index));
    }

    /**
     * 删除一组记录
     *
     * @param indexes 记录的索引
     * @return 删除结果
     */
    public Result deleteRecord(List<Integer> indexes) {
        for (IndexBlock indexBlock : list) {
            Result result = indexBlock.deleteRecord(indexes);
            if (result.code != ResultFactory.SUCCESS) return result;
        }
        return ResultFactory.buildSuccessResult(null);
    }

    public Result insertRecord(Map<String, Object> recordMap, int index) {
        for (IndexBlock block : list) {
            Object object = recordMap.get(block.field);
            if (object == null) continue;
            Result result = block.insertRecord((Comparable) object, index);
            if (result.code != ResultFactory.SUCCESS) {
                deleteRecord(index);
                return result;
            }
        }
        return ResultFactory.buildSuccessResult(index);
    }

    public IxBlock getRelativeIxBlock(String fieldName) {
        for (IndexBlock block : list) {
            if (!block.field.equals(fieldName)) continue;
            return block.getIxBlock();
        }
        return null;
    }

    public Result deleteAll() {
        list.forEach(indexBlock -> indexBlock.deleteAll());
        return null;
    }

    public IxBlock getIx(String fieldName) {
        for (IndexBlock indexBlock : list) {
            if (indexBlock.field.equals(fieldName)) return indexBlock.getIxBlock();
        }
        return null;
    }
}
