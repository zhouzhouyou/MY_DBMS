package core.table.block;

import util.file.Block;
import util.file.FileUtils;
import util.file.exception.IllegalNameException;

import java.io.IOException;

public class IndexBlock extends Block {
    public String indexName;
    public boolean unique;
    public boolean asc;
    public String fields;
    public String recordFilePath;
    public String indexFilePath;
    public transient IxBlock ixBlock;

    public IndexBlock(String indexName, boolean unique, boolean asc, String fields, String recordFilePath, String indexFilePath) {
        this.indexName = indexName;
        this.unique = unique;
        this.asc = asc;
        this.fields = fields;
        this.recordFilePath = recordFilePath;
        this.indexFilePath = indexFilePath;
    }

    public IxBlock getIxBlock() {
        if (ixBlock == null) {
            try {
                ixBlock = FileUtils.deserialize(indexFilePath);
            } catch (IOException | ClassNotFoundException | IllegalNameException e) {
                e.printStackTrace();
            }
        }
        return ixBlock;
    }
}
