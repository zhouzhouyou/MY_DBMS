package core.table;



public class TableDefineFactory extends TableComponentFactory<DefineBlock,TableDefineCollection> {


    public TableDefineFactory(TableBlock tableBlock) {
        super(tableBlock);
    }

    @Override
    protected TableDefineCollection getInstance(TableBlock tableBlock) {
        return new TableDefineCollection(tableBlock.definePath);
    }

    @Override
    protected String getAbsolutePath(TableBlock tableBlock) {
        return tableBlock.definePath;
    }
}
