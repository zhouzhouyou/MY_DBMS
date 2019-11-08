package core.table;


import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;


public class TableDefineCollection extends TableComponentCollection<DefineBlock> {
    public static final String TABLE_DEFINE_POSTFIX = "tdf";

    public TableDefineCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_DEFINE_POSTFIX);
    }

    public TableDefineCollection(String absolutePath) {
        super(absolutePath);
    }
}
