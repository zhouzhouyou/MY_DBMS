package core.table;

import core.file.Block;

import java.util.Date;

public class DefineBlock extends Block {
    public int fieldOrder;
    public String fieldName;
    public int fieldType;
    public int param;
    public Date lastChangeTime;
    public int integrity;

    public DefineBlock(int fieldOrder, String fieldName, int fieldType, int param, Date lastChangeTime, int integrity) {
        this.fieldOrder = fieldOrder;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.param = param;
        this.lastChangeTime = lastChangeTime;
        this.integrity = integrity;
    }
}
