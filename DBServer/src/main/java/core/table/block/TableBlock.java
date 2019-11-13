package core.table.block;

import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import core.table.factory.TableIndexFactory;
import util.file.Block;
import util.parser.parsers.CreateIndexParser;
import util.parser.parsers.CreateTableParser;
import util.parser.parsers.InsertParser;
import util.result.Result;
import util.table.CreateUtil;
import util.table.IndexUtil;
import util.table.InsertUtil;

import java.util.Date;

public class TableBlock extends Block {
    public String tableName;
    public int recordAmount;
    public int fieldAmount;
    public String definePath;
    public String constraintPath;
    public String recordPath;
    public String indexPath;
    public Date createTime;
    public Date lastChangeTime;
    public transient String path;
    private transient TableConstraintFactory constraintFactory;
    private transient TableDefineFactory defineFactory;
    private transient TableIndexFactory indexFactory;
    public transient CreateTableParser parser;

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

    public TableBlock(CreateTableParser parser, String path) {
        this.parser = parser;
        this.path = path;
        String directoryPath = path + parser.getTableName() + "/" + parser.getTableName();
        this.tableName = parser.getTableName();
        this.recordAmount = 0;
        this.fieldAmount = parser.getTableDefine().size();
        this.definePath = directoryPath + ".tdf";
        this.constraintPath = directoryPath + ".tic";
        this.recordPath = directoryPath + ".trd";
        this.indexPath = directoryPath + ".tix";
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

    public Result create() {
        CreateUtil createUtil = CreateUtil.INSTANCE;
        return createUtil.createTable(this);
    }

    public Result insert(InsertParser parser) {
        return InsertUtil.INSTANCE.insert(this, parser);
    }

    public Result createIndex(CreateIndexParser parser) {
        return IndexUtil.INSTANCE.createIndex(this, parser);
    }
}

