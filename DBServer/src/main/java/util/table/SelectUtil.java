package util.table;

import core.table.block.DefineBlock;
import core.table.block.TableBlock;
import core.table.factory.TableFactory;
import util.file.RandomAccessFiles;
import util.pair.Pair;
import util.parser.parsers.SelectParser;
import util.result.Result;
import util.result.ResultFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SelectUtil {
    private Map<String, TableBlock> tableBlocks;
    private SelectParser parser;
    private TableFactory tableFactory;
    private Map<String, List<Object>> records;
    private Map<String, List<String>> fieldMap;

    public SelectUtil(TableFactory tableFactory, SelectParser parser) {
        this.tableFactory = tableFactory;
        this.parser = parser;
        tableBlocks = new HashMap<>();
        records = new HashMap<>();
        fieldMap = new HashMap<>();
    }

    public Result select() throws Exception {
        List<String> tableNames = parser.getTable();

        if (tableNames.size() == 1) return singleTableSelect();

        //put all used fields in a map
        for (String tableName : tableNames) {
            if (!tableFactory.exists(tableName)) return ResultFactory.buildObjectNotExistsResult(tableName);
            TableBlock tableBlock = tableFactory.getTable(tableName);
            for (DefineBlock defineBlock : tableBlock.getDefineFactory().getCollection().list) {
                String fieldName = defineBlock.fieldName;
                List<String> tableList = fieldMap.get(fieldName);
                if (tableList == null) tableList = new LinkedList<>();
                tableList.add(tableName);
                fieldMap.put(fieldName, tableList);
            }
            Map<String, List<Object>> map = Pair.buildAllPairList(tableBlock.getDefineFactory().getFieldNames(), tableBlock.getRaf().select());
            tableBlocks.put(tableName, tableFactory.getTable(tableName));
        }

        List<String> fieldNames = parser.getSelectItem();
        for (String fieldName : fieldNames) {
            String selectFieldName;
            TableBlock tableBlock;
            if (fieldName.contains(".")) {
                String[] strings = fieldName.split("\\.");
                String selectTableName = strings[0];
                selectFieldName = strings[1];
                tableBlock = tableBlocks.get(selectTableName);
                if (tableBlock == null) return ResultFactory.buildObjectNotExistsResult(selectTableName);
            } else {
                selectFieldName = fieldName;
                List<String> tableList = fieldMap.get(selectFieldName);
                if (tableList.size() > 1) return ResultFactory.buildFailResult(selectFieldName);
                tableBlock = tableBlocks.get(tableList.get(0));
            }
            if (!tableBlock.getDefineFactory().exists(selectFieldName)) return ResultFactory.buildObjectNotExistsResult(fieldName);
        }

        return null;
    }

    private Result singleTableSelect() throws Exception {
        String tableName = parser.getTable().get(0);
        TableBlock tableBlock = tableFactory.getTable(tableName);
        RandomAccessFiles raf = tableBlock.getRaf();
        List<List<Object>> allData = raf.select();
        List<String> allFieldNames = tableBlock.getDefineFactory().getFieldNames();
        List<String> fieldNames = parser.getSelectItem();
        if (fieldNames.get(0).equals("*")) fieldNames = allFieldNames;
        Map<String, List<Object>> columns = new HashMap<>();
        List<Map<String, Object>> output = new ArrayList<>();
        for (List<Object> data : allData) {
            Map<String, Object> recordMap = new HashMap<>();
            for (int i = 0; i < data.size(); i++) {
                recordMap.put(allFieldNames.get(i), data.get(i));
            }
            if (parser.hasWhereCondition()) {
                Result result = CheckUtil.check(recordMap, parser.getWhereCondition());
                if (result.code == ResultFactory.NOT_FOUND) return result;
                else if (result.code == ResultFactory.SUCCESS) {
                    Map<String, Object> objectMap = new HashMap<>();
                    fieldNames.forEach(s -> objectMap.put(s, recordMap.get(s)));
                    output.add(objectMap);
                }
            } else {
                Map<String, Object> objectMap = new HashMap<>();
                fieldNames.forEach(s -> objectMap.put(s, recordMap.get(s)));
                output.add(objectMap);
            }
        }
        fieldNames.forEach(fieldName -> {
            List<Object> list = output.stream().map(map -> map.get(fieldName)).collect(Collectors.toCollection(LinkedList::new));
            columns.put(fieldName, list);
        });
        return ResultFactory.buildSuccessResult(columns);
    }
}
