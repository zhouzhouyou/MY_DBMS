package util.table;

import core.table.block.ConstraintBlock;
import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import util.SQL;
import util.parser.parsers.CreateTableParser;
import util.result.Result;
import util.result.ResultFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.table.FieldTypes.*;

public enum CreateUtil {
    INSTANCE;

    public Map<String, Integer> fieldTypeMap = new HashMap<>();
    public Map<String, Integer> constraintTypeMap = new HashMap<>();

    CreateUtil() {
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

    public Result createTable(TableBlock block) {
        CreateTableParser parser = block.parser;
        List<String> tableDefine = parser.getTableDefine();

        List<DefineBlock> defineBlocks = new ArrayList<>();
        List<ConstraintBlock> constraintBlocks = new ArrayList<>();
        for (int i = 0; i < tableDefine.size(); i++) {
            String s = tableDefine.get(i).trim().replaceAll("\\s+", " ");
            String[] strings = s.split(" ");
            String fieldName = strings[0];
            String fieldType = strings[1];
            int type, param = 0;
            if (fieldType.contains("(")) {
                type = VARCHAR;
                param = getParameter(fieldType);
            } else {
                if (!fieldTypeMap.containsKey(fieldType)) return ResultFactory.buildObjectNotExistsResult(fieldName);
                type = fieldTypeMap.get(fieldType);
            }
            defineBlocks.add(new DefineBlock(i, fieldName, type, param, new Date(), 0));

            String cname = "sys_" + block.tableName + "_" + fieldName + "_";
            if (s.contains(SQL.PRIMARY_KEY)) {
                constraintBlocks.add(new ConstraintBlock(cname + "pk", fieldName, PK, ""));
            }
            if (s.contains(SQL.FOREIGN_KEY)) {
                constraintBlocks.add(new ConstraintBlock(cname + "fk", fieldName, FK, ""));
            }
            if (s.contains(SQL.CHECK)) {
                String parameter = getCheck(s);
                constraintBlocks.add(new ConstraintBlock(cname + "check", fieldName, CHECK, parameter));
            }
            if (s.contains(SQL.UNIQUE)) {
                constraintBlocks.add(new ConstraintBlock(cname + "unique", fieldName, UNIQUE, ""));
            }
            if (s.contains(SQL.NOT_NULL)) {
                constraintBlocks.add(new ConstraintBlock(cname + "not_null", fieldName, NOT_NULL, ""));
            }
            if (s.contains(SQL.DEFAULT)) {
                Result convertResult = getDefault(s, type);
                if (convertResult.code != ResultFactory.SUCCESS) return convertResult;
                constraintBlocks.add(new ConstraintBlock(cname + "default", fieldName, DEFAULT, convertResult.data));
            }
        }
        TableDefineFactory defineFactory = block.getDefineFactory();
        defineBlocks.forEach(defineBlock -> defineFactory.add(defineBlock.fieldName, defineBlock));
        defineFactory.saveInstance();

        TableConstraintFactory constraintFactory = block.getConstraintFactory();
        constraintBlocks.forEach(constraintBlock -> constraintFactory.add(constraintBlock.constraintName, constraintBlock));
        constraintFactory.saveInstance();
        return ResultFactory.buildSuccessResult(parser.getTableName());
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
