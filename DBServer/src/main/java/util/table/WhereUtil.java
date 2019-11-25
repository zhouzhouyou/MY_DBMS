package util.table;

import core.table.block.TableBlock;
import util.file.RandomAccessFiles;
import util.pair.Pair;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhereUtil {
    //    private Stack<String> opStack;
//    private Stack<List<Integer>> resultStack;
//    private RandomAccessFiles raf;
//    private TableBlock tableBlock;
//    private String whereSQL;
//
//    public WhereUtil(TableBlock tableBlock, String whereSQL) {
//        this.tableBlock = tableBlock;
//        this.whereSQL = whereSQL;
//        opStack = new Stack<>();
//        resultStack = new Stack<>();
//        raf = tableBlock.getRaf();
//
//    }
//
//    public List<Integer> getWhere() {
//        String[] conditions = whereSQL.split(String.format(SQL.WITH_DELIMITER, SQL.AND_OR_SPLIT));
//
//        for (String string : conditions) {
//            string = string.trim();
//            if (string.equals(" ") || string.equals("")) continue;
//            if (isOperator(string)) addOp(string);
//            else resultStack.push(getIndexes(string));
//        }
//        while (!opStack.empty()) {
//            String top = opStack.pop();
//            operation(top);
//        }
//        return resultStack.get(0);
//        List<Integer> list = new ArrayList<>();
//        try {
//            List<List<Object>> allData = raf.select();
//
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//    private List<Integer> getIndexes(String string) {
//        Parser parser = ConditionParserFactory.grenadeConditionParser(string);
//        if (parser instanceof InCondition) return getInConditionIndexes((InCondition) parser);
//        if (parser instanceof ComparisionCondition) return getComparisionIndexes((ComparisionCondition) parser);
//        return null;
//    }
//
//    private List<Integer> getComparisionIndexes(ComparisionCondition parser) {
//        String fieldName = parser.getLeftValue();
//        String key = parser.getRightValue();
//        Object object = getValue(key);
//
////        if (defineFactory.exists(key)) Object keyObject =
//        IxBlock ixBlock = tableBlock.getIndexFactory().getIx(fieldName);
//        if (ixBlock != null) {
//
//            if (object == null) return null;
//            switch (parser.getCmp()) {
//                case "<":
//                    return ixBlock.lower((Comparable) object, true);
//                case "<=":
//                    return ixBlock.lower((Comparable) object, false);
//                case ">":
//                    return ixBlock.larger((Comparable) object, true);
//                case ">=":
//                    return ixBlock.larger((Comparable) object, false);
//                case "==":
//                    return ixBlock.get((Comparable) object, true);
//                case "<>":
//                case "!=":
//                    return ixBlock.get((Comparable) object, false);
//                default:
//                    return null;
//            }
//        }
//
//        try {
//            List<Object> records = raf.selectField(fieldName);
//            List<Integer> indexes = new LinkedList<>();
//            for (int i = 0; i < records.size(); i++) {
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private List<Integer> getInConditionIndexes(InCondition parser) {
//        return null;
//    }
//
//    private boolean isOperator(String string) {
//        switch (string) {
//            case AND:
//            case OR:
//            case LB:
//            case RB:
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    private void addOp(String op) {
//        if (op.equals(LB)) {
//            opStack.push(op);
//            return;
//        }
//        if (op.equals(RB)) {
//            String top = opStack.pop();
//            while (!top.equals(LB)) {
//                operation(top);
//                top = opStack.pop();
//            }
//            return;
//        }
//        opStack.push(op);
//    }
//
//    private void operation(String top) {
//        if (top.equals(LB) || top.equals(RB)) return;
//        List<Integer> list1 = resultStack.pop();
//        List<Integer> list2 = resultStack.pop();
//
//        if (top.equals(AND)) {
//            List<Integer> util.result = new ArrayList<>();
//            for (int index : list1) {
//                if (list2.contains(index)) util.result.add(index);
//            }
//            resultStack.push(util.result);
//        } else if (top.equals(OR)){
//            Set<Integer> set = new HashSet<>();
//            set.addAll(list1);
//            set.addAll(list2);
//            resultStack.add(new ArrayList<>(set));
//        }
//    }
//
//    private Object getValue(String name) {
//        if (name.startsWith("'")) {
//            if (name.endsWith("'")) return ConvertUtil.getString(name);
//            return null;
//        } else {
////            if (name.endsWith("'")) return null;
////            DefineBlock defineBlock = tableBlock.getDefineFactory().get(name);
////            if (defineBlock != null) {
////
////            }
////            Object object = ConvertUtil.getConvertedObject(key, defineBlock.fieldType);
////            Object o = recordMap.get(name);
////            return o == null ? name : String.valueOf(o);
//        }
//        return null;
//    }
    public static Result getWhere(TableBlock tableBlock, String whereCondition) {
        List<Integer> indexes = new ArrayList<>();
        RandomAccessFiles raf = tableBlock.getRaf();
        try {
            List<List<Object>> allData = raf.select();
            List<String> allFields = tableBlock.getDefineFactory().getFieldNames();
            for (int i = 0; i < allData.size(); i++) {
                List<Object> record = allData.get(i);
                Map<String, Object> recordMap = Pair.buildMap(allFields, record);
                Result result = CheckUtil.check(recordMap, whereCondition);
                if (result.code == ResultFactory.NOT_FOUND) return result;
                else if (result.code == ResultFactory.SUCCESS) indexes.add(i);
            }
            return ResultFactory.buildSuccessResult(indexes);
        } catch (IOException e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }
}
