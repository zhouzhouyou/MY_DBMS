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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static util.table.FieldTypes.*;

public enum  CreateUtil {
    INSTANCE;

    public Map<String, Integer> fieldTypeMap = new HashMap<>();
    public Map<String, Integer> constraintTypeMap = new HashMap<>();

//    public static final int INTEGER = 0;
//    public static final int BOOL = 1;
//    public static final int DOUBLE = 2;
//    public static final int VARCHAR = 3;
//    public static final int DATETIME = 4;
//
//    public static final int PK = 0;
//    public static final int FK = 1;
//    public static final int CHECK = 2;
//    public static final int UNIQUE = 3;
//    public static final int NOT_NULL = 4;
//    public static final int DEFAULT = 5;
//    public static final int IDENTITY = 6;


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
        TableDefineFactory defineFactory = block.getDefineFactory();
        TableConstraintFactory constraintFactory = block.getConstraintFactory();
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
                type = fieldTypeMap.get(strings[1]);
            }
            DefineBlock defineBlock = new DefineBlock(i, fieldName, type, param, new Date(), 0);
            defineFactory.add(defineBlock);

            String cname = "sys_" + block.tableName + "_" + fieldName + "_";
            if (s.contains(SQL.PRIMARY_KEY)) {
                ConstraintBlock constraintBlock = new ConstraintBlock(cname + "pk", fieldName, PK, "");
                constraintFactory.add(constraintBlock);
            }
            if (s.contains(SQL.FOREIGN_KEY)) {
                ConstraintBlock constraintBlock = new ConstraintBlock(cname + "fk", fieldName, FK, "");
                constraintFactory.add(constraintBlock);
            }
            if (s.contains(SQL.CHECK)) {
                String parameter = getCheck(s);
                ConstraintBlock constraintBlock = new ConstraintBlock(cname + "check", fieldName, CHECK, parameter);
                constraintFactory.add(constraintBlock);
            }
            if (s.contains(SQL.UNIQUE)) {
                ConstraintBlock constraintBlock = new ConstraintBlock(cname + "unique", fieldName, UNIQUE, "");
                constraintFactory.add(constraintBlock);
            }
            if (s.contains(SQL.NOT_NULL)) {
                ConstraintBlock constraintBlock = new ConstraintBlock(cname + "not_null", fieldName, NOT_NULL, "");
                constraintFactory.add(constraintBlock);
            }
            if (s.contains(SQL.DEFAULT)) {
                String parameter = getDefault(s);
                ConstraintBlock constraintBlock = new ConstraintBlock(cname + "default", fieldName, DEFAULT, parameter);
                constraintFactory.add(constraintBlock);
            }
        }
        defineFactory.saveInstance();
        constraintFactory.saveInstance();
        return ResultFactory.buildSuccessResult(parser.getTableName());
    }

    static String getDefault(String s) {
        String reg = "(default )(.+)";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(s);
        matcher.find();
        return matcher.group(2);
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
