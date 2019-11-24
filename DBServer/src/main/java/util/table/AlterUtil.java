package util.table;

import core.table.block.ConstraintBlock;
import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import util.SQL;
import util.file.BlockCollections;
import util.file.exception.IllegalNameException;
import util.parser.parsers.AlterTableParser;
import util.parser.parsers.CreateTableParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.table.FieldTypes.*;

public enum AlterUtil {
    INSTANCE;
    public Map<String, Integer> fieldTypeMap = new HashMap<>();
    public Map<String, Integer> constraintTypeMap = new HashMap<>();

    AlterUtil() {
        fieldTypeMap.put(SQL.INTEGER, INTEGER);
        fieldTypeMap.put("int", INTEGER);
        fieldTypeMap.put(SQL.BOOL, BOOL);
        fieldTypeMap.put(SQL.DOUBLE, DOUBLE);
        fieldTypeMap.put(SQL.VARCHAR, VARCHAR);
        fieldTypeMap.put(SQL.DATETIME, DATETIME);
        fieldTypeMap.put("date", DATETIME);

        constraintTypeMap.put(SQL.PRIMARY_KEY, PK);
        constraintTypeMap.put(SQL.FOREIGN_KEY, FK);
        constraintTypeMap.put(SQL.CHECK, CHECK);
        constraintTypeMap.put(SQL.UNIQUE, UNIQUE);
        constraintTypeMap.put(SQL.NOT_NULL, NOT_NULL);
        constraintTypeMap.put(SQL.DEFAULT, DEFAULT);
        constraintTypeMap.put(SQL.IDENTITY, IDENTITY);
    }

    public Result alterTable(TableBlock tableBlock, AlterTableParser parser) throws IllegalNameException, IOException, ClassNotFoundException {
        Result result = null;
        CreateTableParser createTableParser = tableBlock.parser;
        List<DefineBlock> defineBlocks = (List<DefineBlock>) BlockCollections.deserialize(tableBlock.definePath);
        List<ConstraintBlock> constraintBlocks = (List<ConstraintBlock>) BlockCollections.deserialize(tableBlock.constraintPath);

        AlterTableParser.alterType operationType = parser.getAlterType();
        String operationContent = parser.getOperationContent();
        String[] strings = operationContent.split(" ");
        String name = strings[0];
        switch (operationType) {

            case ADD_COLUMN:
                result = addDefineBlock(strings, defineBlocks);
                TableDefineFactory defineFactory = tableBlock.getDefineFactory();
                defineBlocks.forEach(defineBlock -> defineFactory.add(defineBlock.fieldName, defineBlock));
                defineFactory.saveInstance();
                break;
            case DROP_COLUMN:
                result = deleteDefineBlock(strings, defineBlocks);
                break;
            case ADD_CONSTRAINT:
                DefineBlock defineBlock = getModifyDefine(name, defineBlocks);
                break;
            case DROP_CONSTRAINT:
                result = addConstraintBlock(operationContent, constraintBlocks, tableBlock.tableName);
                TableConstraintFactory constraintFactory = tableBlock.getConstraintFactory();
                constraintBlocks.forEach(constraintBlock -> constraintFactory.add(constraintBlock.constraintName, constraintBlock));
                constraintFactory.saveInstance();
                break;
            case MODIFY_COLUMN:
                result = deleteConstraintBlock(operationContent, constraintBlocks);
                break;
            case MODIFY_CONSTRAINT:
                ConstraintBlock constraintBlock = getModifyConstraint(name, constraintBlocks);
                break;
        }

        return result;
    }

    private DefineBlock getModifyDefine(String filedName, List<DefineBlock> defineBlocks) {
        for (DefineBlock defineblock : defineBlocks) {
            if (filedName.equals(defineblock.fieldName)) {
                return defineblock;
            }
        }
        return null;
    }

    private ConstraintBlock getModifyConstraint(String constraintName, List<ConstraintBlock> constraintBlocks) {
        for (ConstraintBlock constraintBlock : constraintBlocks) {
            if (constraintName.equals(constraintBlock.constraintName)) {
                return constraintBlock;
            }
        }
        return null;
    }

    private Result addDefineBlock(String[] contents, List<DefineBlock> defineBlocks) {
        String fieldName = contents[0];
        String fieldType = contents[1];
        int fieldOrder = defineBlocks.size();
        int type, param = 0;

        if (fieldType.contains("(")) {
            type = VARCHAR;
            param = getParameter(fieldType);
        } else {
            if (!fieldTypeMap.containsKey(fieldType)) return ResultFactory.buildObjectNotExistsResult(fieldName);
            type = fieldTypeMap.get(fieldType);
        }
        defineBlocks.add(new DefineBlock(fieldOrder, fieldName, type, param, new Date(), 0));

        return ResultFactory.buildSuccessResult(null);
    }

    private Result addConstraintBlock(String content, List<ConstraintBlock> constraintBlocks, String tableName) {
        String[] contents = content.split(" ");
        String constraintName = contents[0];
        String fieldName = contents[1];
        String cname = "sys_" + tableName + "_" + fieldName + "_";
        if (content.contains(SQL.PRIMARY_KEY)) {
            constraintBlocks.add(new ConstraintBlock(cname + "pk", fieldName, PK, ""));
        }
        if (content.contains(SQL.FOREIGN_KEY)) {
            constraintBlocks.add(new ConstraintBlock(cname + "fk", fieldName, FK, ""));
        }
        if (content.contains(SQL.CHECK)) {
            String parameter = getCheck(content);
            constraintBlocks.add(new ConstraintBlock(cname + "check", fieldName, CHECK, parameter));
        }
        if (content.contains(SQL.UNIQUE)) {
            constraintBlocks.add(new ConstraintBlock(cname + "unique", fieldName, UNIQUE, ""));
        }
        if (content.contains(SQL.NOT_NULL)) {
            constraintBlocks.add(new ConstraintBlock(cname + "not_null", fieldName, NOT_NULL, ""));
        }
        if (content.contains(SQL.DEFAULT)) {
            Result convertResult = getDefault(content, DEFAULT);
            if (convertResult.code != ResultFactory.SUCCESS) return convertResult;
            constraintBlocks.add(new ConstraintBlock(cname + "default", fieldName, DEFAULT, convertResult.data));
        }

        return ResultFactory.buildSuccessResult(null);
    }

    private Result deleteDefineBlock(String[] contents, List<DefineBlock> defineBlocks) {
        String fieldName = contents[0];
        defineBlocks.removeIf(defineBlock -> defineBlock.fieldName.equals(fieldName));
        return ResultFactory.buildSuccessResult(null);
    }

    private Result deleteConstraintBlock(String content, List<ConstraintBlock> constraintBlocks) {
        return ResultFactory.buildSuccessResult(null);
    }

    static Result getDefault(String s, int type) {
        String reg = "(default )(.+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(s);
        matcher.find();
        String result = matcher.group(2);
        return ConvertUtil.getConvertedObject(result, type);
    }

    static String getCheck(String s) {
        String reg = "(check[(])(.+)([)])";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(s);
        matcher.find();
        return matcher.group(2);
    }

    static int getParameter(String type) {
        String answer = type.replaceAll("[^0-9]", "");
        return Integer.parseInt(answer);
    }

}
