package util.table;

import core.table.block.ConstraintBlock;
import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import util.SQL;
import util.file.BlockCollections;
import util.file.RandomAccessFiles;
import util.file.exception.IllegalNameException;
import util.parser.parsers.AlterTableParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.table.FieldTypes.*;

public enum AlterUtil {
    INSTANCE;
    public Map<String, Integer> fieldTypeMap = new HashMap<>();
    public Map<String, Integer> constraintTypeMap = new HashMap<>();

    private List<DefineBlock> defineBlocks;
    private List<ConstraintBlock> constraintBlocks;
    private Result result;
    private String operationContent;
    private String[] operationPieces;
    private TableBlock tableBlock;

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
        this.tableBlock = tableBlock;
        result = null;
        TableConstraintFactory constraintFactory;
        defineBlocks = (List<DefineBlock>) BlockCollections.deserialize(tableBlock.definePath);
        constraintBlocks = (List<ConstraintBlock>) BlockCollections.deserialize(tableBlock.constraintPath);

        AlterTableParser.alterType operationType = parser.getAlterType();
        operationContent = parser.getOperationContent();
        operationPieces = operationContent.split(" ");

        switch (operationType) {
            case ADD_COLUMN:
                result = addDefine();
                break;
            case DROP_COLUMN:
                result = deleteDefine();
                break;
            case ADD_CONSTRAINT:
                result = deleteConstraint();
                break;
            case DROP_CONSTRAINT:
                result = addConstraint();
                break;
            case MODIFY_COLUMN:
                result = modifyDefine();
                break;
            case MODIFY_CONSTRAINT:
                result = modifyConstraint();
                break;
            default:
                break;
        }

        return result;
    }

    private Result modifyDefine() {
        DefineBlock dfBlock = null;
        String fieldName = operationPieces[0];
        for (DefineBlock defineblock : defineBlocks) {
            if (fieldName.equals(defineblock.fieldName)) {
                dfBlock = defineblock;
            }
        }
        if (dfBlock == null)
            return ResultFactory.buildFailResult("No such column.");
        changeRecordAfterModifyColumn(dfBlock);
        TableDefineFactory defineFactory = tableBlock.getDefineFactory();
        defineBlocks.forEach(defineBlock -> defineFactory.add(defineBlock.fieldName, defineBlock));
        defineFactory.saveInstance();
        return ResultFactory.buildSuccessResult(null);
    }

    private Result modifyConstraint() {
        ConstraintBlock conBlock = null;
        String constraintName = operationPieces[0];
        for (ConstraintBlock constraintBlock : constraintBlocks) {
            if (constraintName.equals(constraintBlock.constraintName)) {
                conBlock = constraintBlock;
            }
        }
        if (conBlock == null)
            return ResultFactory.buildFailResult("No such constraint.");

        if (conBlock.constraintType == DEFAULT) {
            DefineBlock defineBlock = null;
            for (DefineBlock dfBlock : defineBlocks) {
                if (dfBlock.fieldName.equals(conBlock.fieldName))
                    defineBlock = dfBlock;
            }

            String newParam = operationPieces[3];
            switch (defineBlock.fieldType) {
                case BOOL:
                    Boolean boolData = ConvertUtil.getBoolean(newParam);
                    if (boolData == null)
                        return ResultFactory.buildFailResult("Wrong data type.");
                    conBlock.param = boolData;
                    break;
                case VARCHAR:
                    String varcharData = ConvertUtil.getString(newParam);
                    if (varcharData == null)
                        return ResultFactory.buildFailResult("Wrong data type.");
                    conBlock.param = varcharData;
                    break;
                case INTEGER:
                    Integer integerData = ConvertUtil.getInteger(newParam);
                    if (integerData == null)
                        return ResultFactory.buildFailResult("Wrong data type.");
                    conBlock.param = integerData;
                    break;
                case DOUBLE:
                    Double doubleData = ConvertUtil.getDouble(newParam);
                    if (doubleData == null)
                        return ResultFactory.buildFailResult("Wrong data type.");
                    conBlock.param = doubleData;
                    break;
                case DATETIME:
                    Date dateData = ConvertUtil.getDate(newParam);
                    if (dateData == null)
                        return ResultFactory.buildFailResult("Wrong data type.");
                    conBlock.param = dateData;
                    break;

            }
        }
        TableConstraintFactory constraintFactory = tableBlock.getConstraintFactory();
        constraintBlocks.forEach(constraintBlock -> constraintFactory.add(constraintBlock.constraintName, constraintBlock));
        constraintFactory.saveInstance();
        return ResultFactory.buildSuccessResult(null);


    }

    private Result addDefine() {
        String fieldName = operationPieces[0];
        String fieldType = operationPieces[1];
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
        changeRecordAfterAddColumn();
        TableDefineFactory defineFactory = tableBlock.getDefineFactory();
        defineBlocks.forEach(defineBlock -> defineFactory.add(defineBlock.fieldName, defineBlock));
        defineFactory.saveInstance();
        return ResultFactory.buildSuccessResult(null);
    }

    private Result addConstraint() {
        boolean containField = false;
        String fieldName = operationPieces[1];
        String cname = operationPieces[0];
        for (DefineBlock defineBlock : defineBlocks) {
            if (defineBlock.fieldName.equals(fieldName))
                containField = true;
        }

        if (!containField)
            return ResultFactory.buildFailResult("No such field.");

        if (operationContent.contains(SQL.PRIMARY_KEY) && checkRecordIfAddConstraint(SQL.PRIMARY_KEY)) {
            constraintBlocks.add(new ConstraintBlock(cname, fieldName, PK, ""));
            return ResultFactory.buildSuccessResult(null);
        }
        if (operationContent.contains(SQL.FOREIGN_KEY) && checkRecordIfAddConstraint(SQL.FOREIGN_KEY)) {
            constraintBlocks.add(new ConstraintBlock(cname, fieldName, FK, ""));
            return ResultFactory.buildSuccessResult(null);
        }
        if (operationContent.contains(SQL.CHECK) && checkRecordIfAddConstraint(SQL.CHECK)) {
            String parameter = getCheck(operationContent);
            constraintBlocks.add(new ConstraintBlock(cname, null, CHECK, parameter));
            return ResultFactory.buildSuccessResult(null);
        }
        if (operationContent.contains(SQL.UNIQUE) && checkRecordIfAddConstraint(SQL.UNIQUE)) {
            constraintBlocks.add(new ConstraintBlock(cname, fieldName, UNIQUE, ""));
            return ResultFactory.buildSuccessResult(null);
        }
        if (operationContent.contains(SQL.NOT_NULL) && checkRecordIfAddConstraint(SQL.NOT_NULL)) {
            constraintBlocks.add(new ConstraintBlock(cname, fieldName, NOT_NULL, ""));
            return ResultFactory.buildSuccessResult(null);
        }
        if (operationContent.contains(SQL.DEFAULT) && checkRecordIfAddConstraint(SQL.DEFAULT)) {
            Result convertResult = getDefault(operationContent, DEFAULT);
            if (convertResult.code != ResultFactory.SUCCESS) return convertResult;
            constraintBlocks.add(new ConstraintBlock(cname, fieldName, DEFAULT, convertResult.data));
            return ResultFactory.buildSuccessResult(null);
        }

        TableConstraintFactory constraintFactory = tableBlock.getConstraintFactory();
        constraintBlocks.forEach(constraintBlock -> constraintFactory.add(constraintBlock.constraintName, constraintBlock));
        constraintFactory.saveInstance();
        return ResultFactory.buildFailResult("The data can't fit your constraint.");


    }

    private Result deleteDefine() {
        String fieldName = operationPieces[0];
        Iterator<DefineBlock> iterator = defineBlocks.iterator();
        while (iterator.hasNext()) {
            DefineBlock defineBlock = iterator.next();
            if (defineBlock.fieldName.equals(fieldName)) {
                changeRecordAfterDropColumn((DefineBlock) iterator);
                iterator.remove();
                TableDefineFactory defineFactory = tableBlock.getDefineFactory();
                defineBlocks.forEach(defBlock -> defineFactory.add(defBlock.fieldName, defineBlock));
                defineFactory.saveInstance();
                return ResultFactory.buildSuccessResult(null);
            }
        }
        return ResultFactory.buildFailResult("This column doesn't exist.");
    }

    private Result deleteConstraint() {
        String constraintName = operationPieces[0];
        Iterator<ConstraintBlock> iterator = constraintBlocks.iterator();
        while (iterator.hasNext()) {
            ConstraintBlock constraintBlock = iterator.next();
            if (constraintBlock.constraintName.equals(constraintName)) {
                iterator.remove();
                TableConstraintFactory constraintFactory = tableBlock.getConstraintFactory();
                constraintBlocks.forEach(conBlock -> constraintFactory.add(conBlock.constraintName, constraintBlock));
                constraintFactory.saveInstance();
                return ResultFactory.buildSuccessResult(null);
            }
        }
        return ResultFactory.buildFailResult("This constraint doesn't exist.");
    }

    private void changeRecordAfterAddColumn() {
        RandomAccessFiles raf = tableBlock.getRaf();
        String fieldName = operationPieces[0];
        String fieldType = operationPieces[1];
        int type, param = 0;
        if (fieldType.contains("(")) {
            type = VARCHAR;
            param = getParameter(fieldType);
        } else {
            type = fieldTypeMap.get(fieldType);
        }
        StringBuilder data;
        switch (type) {
            case VARCHAR:
                data = new StringBuilder();
                for (int i = 0; i < param; i++) {
                    data.append((char) 0);
                }
                raf.addColumnData(data.toString(), param);
                break;
            case INTEGER:
                data = new StringBuilder();
                for (int i = 0; i < 10; i++) {
                    data.append((char) 0);
                }
                raf.addColumnData(Integer.valueOf(data.toString()), 10);
                break;
            case DOUBLE:
                data = new StringBuilder();
                for (int i = 0; i < 16; i++) {
                    data.append((char) 0);
                }
                raf.addColumnData(Double.valueOf(data.toString()), 16);
                break;
            case BOOL:
                data = new StringBuilder();
                data.append((char) 0);
                raf.addColumnData(Boolean.valueOf(data.toString()), 1);
                break;
            case DATETIME:
                data = new StringBuilder();
                for (int i = 0; i < 10; i++) {
                    data.append((char) 0);
                }
                raf.addColumnData(data.toString(), 10);
                break;
            default:
                break;
        }


    }

    private void changeRecordAfterDropColumn(DefineBlock defineBlock) {
        RandomAccessFiles raf = tableBlock.getRaf();
        raf.dropColumnData(defineBlock.fieldOrder, defineBlock.getDataLength());
    }

    private void changeRecordAfterModifyColumn(DefineBlock defineBlock) {
        RandomAccessFiles raf = tableBlock.getRaf();
        String filedName = operationPieces[0];
        String fieldType = operationPieces[1];
        int param = 0;
        if (fieldType.contains("(")) {
            param = getParameter(fieldType);
        }
        raf.changeColumnData(defineBlock.fieldOrder, param, defineBlock.getDataLength());
    }

    private boolean checkRecordIfAddConstraint(String type) {
        RandomAccessFiles raf = tableBlock.getRaf();
        boolean flag = true;
        String fieldName = operationPieces[1];
        switch (type) {
            case SQL.PRIMARY_KEY:
                for (ConstraintBlock constraintBlock : constraintBlocks) {
                    if (constraintBlock.constraintType == constraintTypeMap.get(SQL.PRIMARY_KEY))
                        flag = false;
                }
                break;
            case SQL.FOREIGN_KEY:
            case SQL.CHECK:
                flag = true;
                break;
            case SQL.NOT_NULL:
                for (ConstraintBlock constraintBlock : constraintBlocks) {
                    if (constraintBlock.fieldName.equals(fieldName) && constraintBlock.constraintType == constraintTypeMap.get(SQL.NOT_NULL))
                        flag = false;
                }
                try {
                    List<Object> dataList = raf.selectField(fieldName);
                    for (Object data : dataList) {
                        if (data == null)
                            flag = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SQL.DEFAULT:
                for (ConstraintBlock constraintBlock : constraintBlocks) {
                    if (constraintBlock.fieldName.equals(fieldName) && constraintBlock.constraintType == constraintTypeMap.get(SQL.DEFAULT))
                        flag = false;
                }
                break;
            case SQL.UNIQUE:
                for (ConstraintBlock constraintBlock : constraintBlocks) {
                    if (constraintBlock.fieldName.equals(fieldName) && constraintBlock.constraintType == constraintTypeMap.get(SQL.NOT_NULL))
                        flag = false;
                }
                try {
                    List<Object> dataList = raf.selectField(fieldName);
                    Set<Object> dataSet = new HashSet<>(dataList);
                    if (dataSet.size() < dataList.size())
                        flag = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return flag;
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
