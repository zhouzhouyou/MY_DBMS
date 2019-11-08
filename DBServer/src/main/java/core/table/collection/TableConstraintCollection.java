package core.table.collection;


import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;
import core.table.block.ConstraintBlock;

public class TableConstraintCollection extends TableComponentCollection<ConstraintBlock> {
    public static final String TABLE_CONSTRAINT_POSTFIX = "tic";

    public TableConstraintCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_CONSTRAINT_POSTFIX);
    }

    public TableConstraintCollection(String absolutePath) {
        super(absolutePath);
    }
}
