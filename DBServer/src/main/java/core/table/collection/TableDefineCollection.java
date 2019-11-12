package core.table.collection;


import core.table.block.DefineBlock;
import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;
import util.result.Result;
import util.result.ResultFactory;

import java.util.ArrayList;
import java.util.List;


public class TableDefineCollection extends TableComponentCollection<DefineBlock> {
    public static final String TABLE_DEFINE_POSTFIX = "tdf";

    public TableDefineCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_DEFINE_POSTFIX);
    }

    public TableDefineCollection(String absolutePath) {
        super(absolutePath);
    }

    public int getTotalDataLength() {
        int totalLength = 0;
        for (DefineBlock defineBlock : list)
            totalLength += defineBlock.getDataLength();
        return totalLength;
    }

    public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        list.forEach(defineBlock -> fieldNames.add(defineBlock.fieldName));
        return fieldNames;
    }

    public List<Integer> getFieldTypes() {
        List<Integer> fieldTypes = new ArrayList<>();
        list.forEach(defineBlock -> fieldTypes.add(defineBlock.fieldType));
        return fieldTypes;
    }

    public Result getFieldTypes(List<String> fieldNames) {
        List<Integer> fieldTypes = new ArrayList<>();
        for (String fieldName : fieldNames) {
            boolean existField = false;
            for (DefineBlock block : list) {
                if (!block.fieldName.equals(fieldName)) continue;
                existField = true;
                fieldTypes.add(block.fieldType);
            }
            if (!existField) return ResultFactory.buildObjectNotExistsResult();
        }
        return ResultFactory.buildSuccessResult(fieldTypes);
    }
}
