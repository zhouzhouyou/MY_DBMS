package core.table;

import core.file.Block;
import java.util.Date;

public class TableBlock extends Block {
    private transient TableConstraintFactory constraintFactory;
    private transient TableDefineFactory defineFactory;
    private transient TableIndexFactory indexFactory;

    public String tableName;
    public int recordAmount;
    public int fieldAmount;
    public String definePath;
    public String constraintPath;
    public String recordPath;
    public String indexPath;
    public Date createTime;
    public Date lastChangeTime;

    public TableBlock(String tableName, int recordAmount, int fieldAmount, String definePath, String constraintPath, String recordPath, String indexPath, Date createTime, Date lastChangeTime) {
        this.tableName = tableName;
        this.recordAmount = recordAmount;
        this.fieldAmount = fieldAmount;
        this.definePath = definePath;
        this.constraintPath = constraintPath;
        this.recordPath = recordPath;
        this.indexPath = indexPath;
        this.createTime = createTime;
        this.lastChangeTime = lastChangeTime;
    }

    public TableIndexFactory getIndexFactory() {
        if (indexFactory == null)
            indexFactory = new TableIndexFactory(this);
        return indexFactory;
    }

    public TableDefineFactory getDefineFactory() {
        if (defineFactory == null)
            defineFactory = new TableDefineFactory(this);
        return defineFactory;
    }

    public TableConstraintFactory getConstraintFactory() {
        if (constraintFactory == null)
            constraintFactory = new TableConstraintFactory(this);
        return constraintFactory;
    }
}

