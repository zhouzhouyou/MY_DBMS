package core.table;

import core.file.Block;

public class IndexBlock extends Block {
    public String IndexName;
    public boolean unique;
    public boolean asc;
    public int[] fieldNumber;
    public String [] fields;
    public String recordFilePath;
    public String indexFilePath;

    public IndexBlock(String indexName, boolean unique, boolean asc, int[] fieldNumber, String[] fields, String recordFilePath, String indexFilePath) {
        IndexName = indexName;
        this.unique = unique;
        this.asc = asc;
        this.fieldNumber = fieldNumber;
        this.fields = fields;
        this.recordFilePath = recordFilePath;
        this.indexFilePath = indexFilePath;
    }
}
