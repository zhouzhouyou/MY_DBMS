package util.table;

import core.table.block.TableBlock;
import util.file.RandomAccessFiles;
import util.parser.parsers.DeleteParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.List;

public class DeleteUtil {
    @SuppressWarnings("unchecked")
    public static Result delete(TableBlock tableBlock, DeleteParser parser) {
        boolean deleteAll = parser.deleteAll();
        if (deleteAll) {
            return deleteAll(tableBlock);
        }
//        WhereUtil whereUtil = new WhereUtil(tableBlock, parser.getWhereCondition());
//        List<Integer> toDelete = whereUtil.getWhere();
        Result result = WhereUtil.getWhere(tableBlock, parser.getWhereCondition());
        if (result.code != ResultFactory.SUCCESS) return result;
        List<Integer> toDelete = (List<Integer>) result.data;
        RandomAccessFiles raf = tableBlock.getRaf();
        for (int index : toDelete) {
            try {
                raf.delete(index);
            } catch (IOException e) {
                return ResultFactory.buildFailResult(e.toString());
            }
        }
        return tableBlock.getIndexFactory().deleteRecord(toDelete);
    }

    private static Result deleteAll(TableBlock tableBlock) {
        return tableBlock.getIndexFactory().deleteAll();
    }
}
