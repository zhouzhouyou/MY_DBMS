package util.table;

import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.collection.TableDefineCollection;
import util.file.RandomAccessFiles;
import util.parser.parsers.UpdateParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateUtil {
    @SuppressWarnings("unchecked")
    public static Result update(TableBlock tableBlock, UpdateParser parser) {
        List<String> updateInfo = parser.getUpdateInfo();
        TableDefineCollection defineCollection = tableBlock.getDefineFactory().getCollection();
        List<String> updateField = new ArrayList<>();
        List<Object> updateData = new ArrayList<>();
        for (String string : updateInfo) {
            String[] assign = string.split("=");
            String fieldName = assign[0].trim();
            DefineBlock defineBlock = defineCollection.getDefineBlock(fieldName);
            if (defineBlock == null) return ResultFactory.buildObjectNotExistsResult(fieldName);
            updateField.add(fieldName);
            Result value = ConvertUtil.getConvertedObject(assign[1].trim(), defineBlock.fieldType);
            if (value == null)
                return ResultFactory.buildInvalidValueConvertResult(FieldTypes.getFieldType(defineBlock.fieldType), assign[1]);
            updateData.add(value.data);
        }
        Result result = WhereUtil.getWhereForOther(tableBlock, parser.getWhereCondition());
        if (result.code != ResultFactory.SUCCESS) return result;
        List<Integer> toUpdate = (List<Integer>) result.data;
        RandomAccessFiles raf = tableBlock.getRaf();


        for (int index : toUpdate) {
            try {
                List<Object> record = raf.select(index);
                for (DefineBlock defineList : defineCollection.list) {
                    for (int i = 0; i < updateField.size(); i++) {
                        if (defineList.fieldName.equals(updateField.get(i)))
                            record.set(defineList.fieldOrder, updateData.get(i));
                    }
//                    if (updateField.get(defineBlock.fieldOrder) != null) {
//                        record.set(defineBlock.fieldOrder, updateData.get(defineBlock.fieldOrder));
//                    }
                }
                raf.update(index, record);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResultFactory.buildSuccessResult("Update Success");
    }
}
