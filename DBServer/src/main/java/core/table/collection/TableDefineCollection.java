package core.table.collection;


import util.file.exception.EmptyNameException;
import util.file.exception.IllegalNameException;
import core.table.block.DefineBlock;


public class TableDefineCollection extends TableComponentCollection<DefineBlock> {
    public static final String TABLE_DEFINE_POSTFIX = "tdf";

    public TableDefineCollection(String relativePath, String prefix) throws EmptyNameException, IllegalNameException {
        super(relativePath, prefix, TABLE_DEFINE_POSTFIX);
    }

    public TableDefineCollection(String absolutePath) {
        super(absolutePath);
    }

    public int getTotalDataLength() {
        int totalLength = 0;
        for (DefineBlock defineBlock : list)
            totalLength += defineBlock.getDataLength();
        return totalLength;
    }
}
