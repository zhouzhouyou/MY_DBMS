package util.table;

import core.table.block.ConstraintBlock;
import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.collection.TableConstraintCollection;
import core.table.collection.TableDefineCollection;
import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import util.SQL;
import util.file.BlockCollections;
import util.file.RandomAccessFiles;
import util.file.exception.IllegalNameException;
import util.parser.parsers.AlterTableParser;
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

    private TableDefineCollection defineCollection;
    private TableConstraintCollection constraintCollection;
    private String operationContent;
    private String[] operationPieces;
    private TableBlock tableBlock;
    private RandomAccessFiles raf;

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
        Result result = null;
        defineCollection = (TableDefineCollection) BlockCollections.deserialize(tableBlock.definePath);
        constraintCollection = (TableConstraintCollection) BlockCollections.deserialize(tableBlock.constraintPath);

        raf = tableBlock.getRaf();
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
                result = addConstraint();
                break;
            case DROP_CONSTRAINT:
                result = deleteConstraint();
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
        String fieldType = operationPieces[1];
        for (DefineBlock defineblock : defineCollection.list) {
            if (fieldName.equals(defineblock.fieldName)) {
                dfBlock = defineblock;
                break;
            }
        }
        if (dfBlock == null)
            return ResultFactory.buildFailResult("No such column.");
        if (dfBlock.fieldType == VARCHAR) {
            int newParam = getParameter(fieldType);
            if (dfBlock.param > newParam)
                return ResultFactory.buildFailResult("You can't lower the size.");
            dfBlock.param = newParam;
        }
        TableDefineFactory defineFactory = tableBlock.getDefineFactory();
        defineFactory.setCollection(defineCollection);
        //defineCollection.list.forEach(defineBlock -> defineFactory.add(defineBlock.fieldName, defineBlock));
        defineFactory.saveInstance();
        changeRecordAfterModifyColumn(dfBlock, defineCollection);
        return ResultFactory.buildSuccessResult(null);
    }

    private Result modifyConstraint() {
        ConstraintBlock conBlock = null;
        String constraintName = operationPieces[0];
        for (ConstraintBlock constraintBlock : constraintCollection.list) {
            if (constraintName.equals(constraintBlock.constraintName)) {
                conBlock = constraintBlock;
            }
        }
        if (conBlock == null)
            return ResultFactory.buildFailResult("No such constraint.");

        if (conBlock.constraintType == DEFAULT) {
            DefineBlock defineBlock = null;
            for (DefineBlock dfBlock : defineCollection.list) {
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
        if(conBlock.constraintType == CHECK){
            conBlock.param = getCheck(operationContent.substring(operationContent.indexOf(operationPieces[1])));
        }
        TableConstraintFactory constraintFactory = tableBlock.getConstraintFactory();
        //constraintCollection.list.forEach(constraintBlock -> constraintFactory.add(constraintBlock.constraintName, constraintBlock));
        constraintFactory.setCollection(constraintCollection);
        constraintFactory.saveInstance();
        return ResultFactory.buildSuccessResult(null);


    }

    private Result addDefine() {
        String fieldName = operationPieces[0];
        String fieldType = operationPieces[1];
        int fieldOrder = defineCollection.list.size();
        int type, param = 0;

        if (fieldType.contains("(")) {
            type = VARCHAR;
            param = getParameter(fieldType);
        } else {
            if (!fieldTypeMap.containsKey(fieldType)) return ResultFactory.buildObjectNotExistsResult(fieldName);
            type = fieldTypeMap.get(fieldType);
        }
        defineCollection.add(new DefineBlock(fieldOrder, fieldName, type, param, new Date(), 0));
        TableDefineFactory defineFactory = tableBlock.getDefineFactory();
        //defineCollection.list.forEach(defineBlock -> defineFactory.add(defineBlock.fieldName, defineBlock));
        defineFactory.setCollection(defineCollection);
        defineFactory.saveInstance();
        changeRecordAfterAddColumn(defineCollection);
        return ResultFactory.buildSuccessResult(null);
    }

    private Result addConstraint() {
        DefineBlock containField = null;
        String fieldName = operationPieces[1];
        String cname = operationPieces[0];
        for (DefineBlock defineBlock : defineCollection.list) {
            if (defineBlock.fieldName.equals(fieldName))
                containField = defineBlock;
        }



        for (ConstraintBlock constraintBlock : constraintCollection.list) {
            if (constraintBlock.constraintName.equals(cname))
                return ResultFactory.buildFailResult("Constraint name already exists.");
        }

        if (operationContent.contains(SQL.PRIMARY_KEY) && checkRecordIfAddConstraint(SQL.PRIMARY_KEY)) {
            if (containField == null)
                return ResultFactory.buildFailResult("No such field.");
            constraintCollection.add(new ConstraintBlock(cname, fieldName, PK, ""));

        } else if (operationContent.contains(SQL.FOREIGN_KEY) && checkRecordIfAddConstraint(SQL.FOREIGN_KEY)) {
            if (containField == null)
                return ResultFactory.buildFailResult("No such field.");
            constraintCollection.add(new ConstraintBlock(cname, fieldName, FK, ""));

        } else if (operationContent.contains(SQL.CHECK) && checkRecordIfAddConstraint(SQL.CHECK)) {
            String parameter = getCheck(operationContent.substring(operationContent.indexOf(operationPieces[1])));
            constraintCollection.add(new ConstraintBlock(cname, null, CHECK, parameter));

        } else if (operationContent.contains(SQL.UNIQUE) && checkRecordIfAddConstraint(SQL.UNIQUE)) {
            if (containField == null)
                return ResultFactory.buildFailResult("No such field.");
            constraintCollection.add(new ConstraintBlock(cname, fieldName, UNIQUE, ""));

        } else if (operationContent.contains(SQL.NOT_NULL) && checkRecordIfAddConstraint(SQL.NOT_NULL)) {
            if (containField == null)
                return ResultFactory.buildFailResult("No such field.");
            constraintCollection.add(new ConstraintBlock(cname, fieldName, NOT_NULL, ""));

        } else if (operationContent.contains(SQL.DEFAULT) && checkRecordIfAddConstraint(SQL.DEFAULT)) {
            if (containField == null)
                return ResultFactory.buildFailResult("No such field.");
            String data = operationPieces[3];
            int type = containField.fieldType;
            Result convertResult = getDefault("default " + data, type);
            if (convertResult.code != ResultFactory.SUCCESS) return convertResult;
            constraintCollection.add(new ConstraintBlock(cname, fieldName, DEFAULT, convertResult.data));
        }else
            return ResultFactory.buildFailResult("Constraint not fit.");

        TableConstraintFactory constraintFactory = tableBlock.getConstraintFactory();
        //constraintCollection.list.forEach(constraintBlock -> constraintFactory.add(constraintBlock.constraintName, constraintBlock));
        constraintFactory.setCollection(constraintCollection);
        constraintFactory.saveInstance();
        return ResultFactory.buildSuccessResult("Success to add a constraint");


    }

    private Result deleteDefine() {
        String fieldName = operationPieces[0];
        Iterator<DefineBlock> iterator = defineCollection.list.iterator();
        while (iterator.hasNext()) {
            DefineBlock defineBlock = iterator.next();
            if (defineBlock.fieldName.equals(fieldName)) {
                iterator.remove();
                TableDefineFactory defineFactory = tableBlock.getDefineFactory();
                //defineCollection.list.forEach(defBlock -> defineFactory.add(defBlock.fieldName, defineBlock));
                defineFactory.setCollection(defineCollection);
                defineFactory.saveInstance();
                changeRecordAfterDropColumn(defineBlock, defineCollection);
                return ResultFactory.buildSuccessResult(null);
            }
        }
        return ResultFactory.buildFailResult("This column doesn't exist.");
    }

    private Result deleteConstraint() {
        String constraintName = operationPieces[0];
        Iterator<ConstraintBlock> iterator = constraintCollection.list.iterator();
        while (iterator.hasNext()) {
            ConstraintBlock constraintBlock = iterator.next();
            if (constraintBlock.constraintName.equals(constraintName)) {
                iterator.remove();
                TableConstraintFactory constraintFactory = tableBlock.getConstraintFactory();
                //constraintCollection.list.forEach(conBlock -> constraintFactory.add(conBlock.constraintName, constraintBlock));
                constraintFactory.setCollection(constraintCollection);
                constraintFactory.saveInstance();
                return ResultFactory.buildSuccessResult("Success to drop a constraint");
            }
        }
        return ResultFactory.buildFailResult("This constraint doesn't exist.");
    }

    private void changeRecordAfterAddColumn(TableDefineCollection defineCollection) {
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
        String inputString = "";

        switch (type) {
            case VARCHAR:
                data = new StringBuilder();
                for (int i = 0; i < param; i++) {
                    data.append((char) 0);
                }
                inputString += data;
                raf.addColumnData(ConvertUtil.getString(inputString), defineCollection);
                break;
            case INTEGER:
                data = new StringBuilder();
                for (int i = 0; i < 10; i++) {
                    data.append((char) 0);
                }
                inputString += data;
                raf.addColumnData(ConvertUtil.getInteger(inputString), defineCollection);
                break;
            case DATETIME:
                data = new StringBuilder();
                for (int i = 0; i < 10; i++) {
                    data.append((char) 0);
                }
                inputString += data;
                raf.addColumnData(ConvertUtil.getDate(inputString), defineCollection);
                break;
            case DOUBLE:
                data = new StringBuilder();
                for (int i = 0; i < 16; i++) {
                    data.append((char) 0);
                }
                inputString += data;
                raf.addColumnData(ConvertUtil.getDouble(inputString), defineCollection);
                break;
            case BOOL:
                data = new StringBuilder();
                data.append((char) 0);
                inputString += data;
                raf.addColumnData(ConvertUtil.getBoolean(inputString), defineCollection);
                break;
            default:
                break;
        }


    }

    private void changeRecordAfterDropColumn(DefineBlock defineBlock, TableDefineCollection defineCollection) {
        raf.dropColumnData(defineBlock.fieldOrder, defineCollection);
    }

    private void changeRecordAfterModifyColumn(DefineBlock defineBlock, TableDefineCollection defineCollection) {
        String filedName = operationPieces[0];
        String fieldType = operationPieces[1];
        int param = 0;
        if (fieldType.contains("(")) {
            param = getParameter(fieldType);
        }
        raf.changeColumnData(defineBlock.fieldOrder, param, defineCollection);
    }

    private boolean checkRecordIfAddConstraint(String type) {
        boolean flag = true;
        String fieldName = operationPieces[1];
        switch (type) {
            case SQL.PRIMARY_KEY:
                for (ConstraintBlock constraintBlock : constraintCollection.list) {
                    if (constraintBlock.constraintType == constraintTypeMap.get(SQL.PRIMARY_KEY))
                        flag = false;
                }
                break;
            case SQL.FOREIGN_KEY:
            case SQL.CHECK:
                flag = true;
                break;
            case SQL.NOT_NULL:
                for (ConstraintBlock constraintBlock : constraintCollection.list) {
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
                for (ConstraintBlock constraintBlock : constraintCollection.list) {
                    if (constraintBlock.fieldName.equals(fieldName) && constraintBlock.constraintType == constraintTypeMap.get(SQL.DEFAULT))
                        flag = false;
                }
                break;
            case SQL.UNIQUE:
                for (ConstraintBlock constraintBlock : constraintCollection.list) {
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
