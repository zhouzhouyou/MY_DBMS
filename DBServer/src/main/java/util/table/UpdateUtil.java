package util.table;

import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.collection.TableDefineCollection;
import util.file.RandomAccessFiles;
import util.parser.parsers.UpdateParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.List;

public class UpdateUtil {
    public static Result update(TableBlock tableBlock, UpdateParser parser) {
        List<String> updateInfo = parser.getUpdateInfo();
        TableDefineCollection defineCollection = tableBlock.getDefineFactory().getCollection();

        for (String string : updateInfo) {
            String[] assign = string.split("=");
            String fieldName = assign[0];
            DefineBlock defineBlock = defineCollection.getDefineBlock(fieldName);
            if (defineBlock == null) return ResultFactory.buildObjectNotExistsResult(fieldName);
            Object value = ConvertUtil.getConvertedObject(assign[1], defineBlock.fieldType);
            if (value == null) return ResultFactory.buildInvalidValueConvertResult(FieldTypes.getFieldType(defineBlock.fieldType), assign[1]);
        }

        List<Integer> toUpdate = WhereUtil.getWhere(tableBlock, parser.getWhereCondition());
        RandomAccessFiles raf = tableBlock.getRaf();

        for (int index : toUpdate) {
            try {
                List<Object> record = raf.select(index);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
