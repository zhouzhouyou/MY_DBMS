package core.table.block;

import util.SQL;
import util.file.Block;
import util.file.RandomAccessFiles;
import util.result.Result;
import util.result.ResultFactory;
import util.table.CheckUtil;
import util.table.FieldTypes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 存储着一条约束
 *
 * @see core.table.factory.TableConstraintFactory
 * @see core.table.collection.TableConstraintCollection
 */
public class ConstraintBlock extends Block {
    /**
     * 约束名
     */
    public String constraintName;
    /**
     * 约束作用的域名
     */
    public String fieldName;
    /**
     * 约束类型
     *
     * @see util.table.FieldTypes
     */
    public int constraintType;
    /**
     * default约束或者check
     */
    public Object param;

    public ConstraintBlock(String constraintName, String fieldName, int constraintType, Object param) {
        this.constraintName = constraintName;
        this.fieldName = fieldName;
        this.constraintType = constraintType;
        this.param = param;
    }

    public Result check(Map<String, Object> recordMap, List<Integer> fieldTypes, RandomAccessFiles raf) {
        //TODO: 判断是否成功
        switch (constraintType) {
            case FieldTypes.PK:
                return checkPK(recordMap, raf);
            case FieldTypes.FK:
                //TODO: 外键
                return ResultFactory.buildSuccessResult(null);
            case FieldTypes.NOT_NULL:
                return checkNotNull(recordMap);
            case FieldTypes.UNIQUE:
                return checkUnique(recordMap, raf);
            case FieldTypes.CHECK:
                return checkCheck(recordMap, fieldTypes, raf);
            case FieldTypes.DEFAULT:
                return checkDefault(recordMap);
        }
        return ResultFactory.buildSuccessResult(null);
    }

    private Result checkDefault(Map<String, Object> recordMap) {
        recordMap.computeIfAbsent(fieldName, k -> param);
        return ResultFactory.buildSuccessResult(null);
    }

    private Result checkCheck(Map<String, Object> recordMap, List<Integer> fieldTypes, RandomAccessFiles raf) {
        return CheckUtil.check(recordMap, (String) param);
    }

    private Result checkUnique(Map<String, Object> recordMap, RandomAccessFiles raf) {
        List<Object> pks = null;
        try {
            pks = raf.selectField(fieldName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object pk = recordMap.get(fieldName);
        if (pk == null) return ResultFactory.buildSuccessResult(null);
        if (pks.contains(pk)) return ResultFactory.buildObjectAlreadyExistsResult(SQL.UNIQUE);
        return ResultFactory.buildSuccessResult(null);
    }

    private Result checkNotNull(Map<String, Object> recordMap) {
        Object notNull = recordMap.get(fieldName);
        if (notNull == null) return ResultFactory.buildFailResult(SQL.NOT_NULL);
        return ResultFactory.buildSuccessResult(null);
    }

    private Result checkPK(Map<String, Object> recordMap, RandomAccessFiles raf) {
        List<Object> pks = null;
        try {
            pks = raf.selectField(fieldName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object pk = recordMap.get(fieldName);
        if (pk == null) return ResultFactory.buildFailResult(SQL.NOT_NULL);
        if (pks.contains(pk)) return ResultFactory.buildObjectAlreadyExistsResult(SQL.UNIQUE);
        return ResultFactory.buildSuccessResult(null);
    }
}
