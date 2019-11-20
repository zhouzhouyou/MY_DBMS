package util.table;

import core.table.block.TableBlock;
import util.file.RandomAccessFiles;
import util.parser.parsers.DeleteParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.List;

public class DeleteUtil {
    public static Result delete(TableBlock tableBlock, DeleteParser parser) {
        List<Integer> toDelete = WhereUtil.getWhere(tableBlock, parser.getWhereCondition());
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
}
