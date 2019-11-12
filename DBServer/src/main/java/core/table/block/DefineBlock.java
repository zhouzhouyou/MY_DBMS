package core.table.block;

import util.table.FieldTypes;
import util.file.Block;

import java.util.Date;

public class DefineBlock extends Block implements Comparable<DefineBlock>{
    @Override
    public int compareTo(DefineBlock o) {
        return fieldOrder > o.fieldOrder ? 1 : -1;
    }

    public int fieldOrder;
    public String fieldName;
    public int fieldType;
    public int param;
    public Date lastChangeTime;
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

    public int getDataLength() {
        int length = 0;
        switch (fieldType) {
            case FieldTypes.BOOL:
                length = 1;
                break;
            case FieldTypes.DOUBLE:
                length = 8;
                break;
            case FieldTypes.INTEGER:
                length = 4;
                break;
            case FieldTypes.DATETIME:
                length = 10;
                break;
            case FieldTypes.VARCHAR:
                length = param;
                break;
            default:
                length = 0;
                break;

        }
        return length;
    }
}
