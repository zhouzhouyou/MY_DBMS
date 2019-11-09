package core.table.block;

import util.file.Block;

public class ConstraintBlock extends Block {
    public String constraintName;
    public String filedName;
    public int constraintType;
    public String param;

    public ConstraintBlock(String constraintName, String filedName, int constraintType, String param) {
        this.constraintName = constraintName;
        this.filedName = filedName;
        this.constraintType = constraintType;
        this.param = param;
    }
}
