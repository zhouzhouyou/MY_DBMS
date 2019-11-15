package core.table.collection;


import core.table.block.DefineBlock;
import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;
import util.pair.Pair;
import util.result.Result;
import util.result.ResultFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 表的所有定义
 */
public class TableDefineCollection extends TableComponentCollection<DefineBlock> {
    public static final String TABLE_DEFINE_POSTFIX = "tdf";

    public TableDefineCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_DEFINE_POSTFIX);
    }

    public TableDefineCollection(String absolutePath) {
        super(absolutePath);
    }

    /**
     * 获取表中一条记录所需要的长度
     *
     * @return 一条记录的总长度
     * @see DefineBlock#getDataLength()
     */
    public int getTotalDataLength() {
        int totalLength = 0;
        for (DefineBlock defineBlock : list)
            totalLength += defineBlock.getDataLength();
        return totalLength;
    }

    /**
     * 返回这个域之前的总长度和对应的DefineBlock
     *
     * @param fieldName 域名
     * @return 这个域之前的总长度和对应的DefineBlock
     */
    public Pair<DefineBlock, Integer> getPreInfo(String fieldName) {
        int totalLength = 0;
        for (DefineBlock defineBlock : list) {
            if (!defineBlock.fieldName.equals(fieldName)) totalLength += defineBlock.getDataLength();
            else return new Pair<>(defineBlock, totalLength);
        }
        return null;
    }

    /**
     * 获取域的名字，按顺序排列
     *
     * @return 域的名字
     */
    public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        list.forEach(defineBlock -> fieldNames.add(defineBlock.fieldName));
        return fieldNames;
    }

    /**
     * 返回域的类型
     *
     * @return 返回域的类型
     */
    public List<Integer> getFieldTypes() {
        List<Integer> fieldTypes = new ArrayList<>();
        list.forEach(defineBlock -> fieldTypes.add(defineBlock.fieldType));
        return fieldTypes;
    }

    /**
     * 返回对应域名的域的类型
     *
     * @param fieldNames 域名
     * @return 对应的域的类型
     */
    public Result getFieldTypes(List<String> fieldNames) {
        List<Integer> fieldTypes = new ArrayList<>();
        for (String fieldName : fieldNames) {
            boolean existField = false;
            for (DefineBlock block : list) {
                if (!block.fieldName.equals(fieldName)) continue;
                existField = true;
                fieldTypes.add(block.fieldType);
                break;
            }
            if (!existField) return ResultFactory.buildObjectNotExistsResult();
        }
        return ResultFactory.buildSuccessResult(fieldTypes);
    }
}
