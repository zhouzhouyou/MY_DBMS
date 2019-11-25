package core.table.collection;


import core.table.block.ConstraintBlock;
import util.file.RandomAccessFiles;
import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;
import util.pair.Pair;
import util.result.Result;
import util.result.ResultFactory;

import java.util.List;

/**
 * 表的所有约束
 */
public class TableConstraintCollection extends TableComponentCollection<ConstraintBlock> {
    public static final String TABLE_CONSTRAINT_POSTFIX = "tic";

    public TableConstraintCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_CONSTRAINT_POSTFIX);
    }

    public TableConstraintCollection(String absolutePath) {
        super(absolutePath);
    }

    public Result check(List<Pair<String, Object>> recordMap, List<Integer> fieldTypes, RandomAccessFiles raf) {
        for (ConstraintBlock block : list) {
            Result result = block.check(recordMap, fieldTypes, raf);
            if (result.code != ResultFactory.SUCCESS) return result;
        }
        return ResultFactory.buildSuccessResult(recordMap);
    }
}
