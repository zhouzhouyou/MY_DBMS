package util.table;

import core.table.block.TableBlock;
import core.table.collection.TableDefineCollection;
import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import util.SQL;
import util.parser.parsers.InsertParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public enum InsertUtil {
    INSTANCE;
    private final String AMOUNT_NOT_MATCH = "Field Amount doesn't match";

    @SuppressWarnings("unchecked")
    public Result insert(TableBlock block, InsertParser parser) {
        List<String> fields = parser.getInsertField();
        List<String> values = parser.getInsertValue();
        TableDefineFactory factory = block.getDefineFactory();
        TableConstraintFactory constraintFactory = block.getConstraintFactory();
        TableDefineCollection defineCollection = factory.getCollection();

        /*check whether the field amount is correct*/
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
            switch (types.get(i)) {
                case FieldTypes.BOOL:
                    if (!value.equals("true") && !value.equals("false") && !value.equals("1") && !value.equals("0"))
                        return ResultFactory.buildInvalidValueConvertResult(SQL.BOOL, value);
                    map.put(fieldName, Boolean.valueOf(value));
                    break;
                case FieldTypes.DOUBLE:
                    if (!StringUtils.isNumeric(value))
                        return ResultFactory.buildInvalidValueConvertResult(SQL.DOUBLE, value);
                    map.put(fieldName, Double.valueOf(value));
                    break;
                case FieldTypes.INTEGER:
                    if (!NumberUtils.isDigits(value))
                        return ResultFactory.buildInvalidValueConvertResult(SQL.INTEGER, value);
                    map.put(fieldName, Integer.valueOf(value));
                    break;
                case FieldTypes.DATETIME:
                    Date date = getDate(value);
                    if (date == null)
                        return ResultFactory.buildInvalidValueConvertResult(SQL.DATETIME, value);
                    map.put(fieldName, date);
                    break;
                case FieldTypes.VARCHAR:
                    if (!value.startsWith("'") || !value.endsWith("'"))
                        return ResultFactory.buildInvalidValueConvertResult(SQL.VARCHAR, value);
                    map.put(fieldName, value.substring(1));
                    break;
                default:
                    break;
            }
        }
        Collections.sort(defineCollection.list);
        List<Object> toInsert = new ArrayList<>();
        defineCollection.list.forEach(defineBlock -> toInsert.add(map.get(defineBlock.fieldName)));
        result = constraintFactory.check(toInsert);
        if (result.code == ResultFactory.SUCCESS) {
            try {
                int index = block.raf.insert(toInsert);
                return ResultFactory.buildSuccessResult(index);
            } catch (IOException e) {
                return ResultFactory.buildFailResult(e.toString());
            }
        }
        return result;
    }

    private Date getDate(String value) {
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd");
        sd1.setLenient(true);
        try {
            return sd1.parse(value);
        } catch (ParseException e) {
            //ignore
        }
        SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sd2.setLenient(true);
        try {
            return sd2.parse(value);
        } catch (ParseException e) {
            //ignore
        }
        return null;
    }
}
