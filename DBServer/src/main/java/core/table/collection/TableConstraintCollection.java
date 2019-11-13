package core.table.collection;


import core.table.block.ConstraintBlock;
import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;

public class TableConstraintCollection extends TableComponentCollection<ConstraintBlock> {
    public static final String TABLE_CONSTRAINT_POSTFIX = "tic";

    public TableConstraintCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_CONSTRAINT_POSTFIX);
    }

    public TableConstraintCollection(String absolutePath) {
        super(absolutePath);
    }
}
