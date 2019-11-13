package core.table.block;

import core.table.collection.TableDefineCollection;
import util.file.Block;
import util.table.FieldTypes;

import java.util.Date;

/**
 * 存储着一条定义
 */
public class DefineBlock extends Block implements Comparable<DefineBlock> {
    /**
     * 在表结构中的顺序
     */
    public int fieldOrder;
    /**
     * 域的名字
     */
    public String fieldName;
    /**
     * 域的类型
     *
     * @see FieldTypes
     */
    public int fieldType;
    /**
     * 如果是varchar类型，会有长度限制
     */
    public int param;
    /**
     * 最后修改时间，永远是{@code new Date()}
     */
    public Date lastChangeTime;
    /**
     * 目前无用，本意为完整性约束的数量
     */
    @Deprecated
    public int integrity;

    public DefineBlock(int fieldOrder, String fieldName, int fieldType, int param, Date lastChangeTime, int integrity) {
        this.fieldOrder = fieldOrder;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.param = param;
        this.lastChangeTime = lastChangeTime;
        this.integrity = integrity;
    }

    @Override
    public int compareTo(DefineBlock o) {
        return fieldOrder > o.fieldOrder ? 1 : -1;
    }

    /**
     * 获取该字段需要多长的位数
     *
     * @return 位长
     * @see TableDefineCollection#getTotalDataLength()
     * @see util.file.RandomAccessFiles
     */
    public int getDataLength() {
        int length = 0;
        switch (fieldType) {
            case FieldTypes.BOOL:
                length = 1;
                break;
            case FieldTypes.DOUBLE:
                length = 16;
                break;
            case FieldTypes.INTEGER:
            case FieldTypes.DATETIME:
                length = 10;
                break;
            case FieldTypes.VARCHAR:
                length = param;
                break;
            default:
                break;
        }
        return length;
    }
}
