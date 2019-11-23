package util.table;

import core.table.block.TableBlock;
import core.table.collection.TableDefineCollection;
import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import util.parser.parsers.InsertParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.*;

public class InsertUtil {

    @SuppressWarnings("unchecked")
    public static Result insert(TableBlock block, InsertParser parser) {
        List<String> fields = parser.getInsertField();
        List<String> values = parser.getInsertValue();
        TableDefineFactory factory = block.getDefineFactory();

        TableDefineCollection defineCollection = factory.getCollection();

        /*check whether the field amount is correct*/
        String AMOUNT_NOT_MATCH = "Field Amount doesn't match";
        if (fields.size() == 0) {
            if (values.size() != block.fieldAmount) return ResultFactory.buildFailResult(AMOUNT_NOT_MATCH);
            fields = defineCollection.getFieldNames();
        }
        if (fields.size() != values.size()) return ResultFactory.buildFailResult(AMOUNT_NOT_MATCH);

        /*check whether field name is correct*/
        Result result = defineCollection.getFieldTypes(fields);
        if (result.code != ResultFactory.SUCCESS) return result;

        List<Integer> types = (List<Integer>) result.data;
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < types.size(); i++) {
            String value = values.get(i);
            String fieldName = fields.get(i);
            Result convertResult = ConvertUtil.getConvertedObject(value, types.get(i));
            if (convertResult.code != ResultFactory.SUCCESS) return convertResult;
            map.put(fieldName, convertResult.data);
        }
        Collections.sort(defineCollection.list);
        List<Object> toInsert = new ArrayList<>();
        defineCollection.list.forEach(defineBlock -> toInsert.add(map.get(defineBlock.fieldName)));
        TableConstraintFactory constraintFactory = block.getConstraintFactory();
        result = constraintFactory.check(toInsert);
        if (result.code == ResultFactory.SUCCESS) {
            try {
                int index = block.getRaf().insert(toInsert);
                result = block.getIndexFactory().insertRecord(toInsert, index);
                if (result.code != ResultFactory.SUCCESS) block.getRaf().delete(index);
                return result;
            } catch (IOException e) {
                return ResultFactory.buildFailResult(e.toString());
            }
        }
        return result;
    }
}
