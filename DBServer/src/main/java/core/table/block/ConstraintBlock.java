package core.table.block;

import util.file.Block;
import util.result.Result;
import util.result.ResultFactory;

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
     * default约束独有
     */
    public String param;

    public ConstraintBlock(String constraintName, String fieldName, int constraintType, String param) {
        this.constraintName = constraintName;
        this.fieldName = fieldName;
        this.constraintType = constraintType;
        this.param = param;
    }

    public Result check(Map<String, Object> recordMap) {
        //TODO: 判断是否成功
        return ResultFactory.buildSuccessResult(null);
    }
}
