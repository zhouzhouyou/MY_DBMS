package core.table.block;

import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import core.table.factory.TableIndexFactory;
import util.file.Block;
import util.file.RandomAccessFiles;
import util.parser.parsers.CreateIndexParser;
import util.parser.parsers.CreateTableParser;
import util.parser.parsers.InsertParser;
import util.result.Result;
import util.table.CreateUtil;
import util.table.InsertUtil;

import java.io.IOException;
import java.util.Date;

/**
 * 存储着一张表的信息
 */
public class TableBlock extends Block {
    /**
     * 表名
     */
    public String tableName;
    /**
     * 记录的数目
     */
    public int recordAmount;
    /**
     * 域的数目
     */
    public int fieldAmount;
    /**
     * 定义文件的地址
     */
    public String definePath;
    /**
     * 约束文件的地址
     */
    public String constraintPath;
    /**
     * 记录文件的地址
     */
    public String recordPath;
    /**
     * 索引文件的地址
     */
    public String indexPath;
    /**
     * 创建时间
     */
    public Date createTime;
    /**
     * 最后修改时间
     */
    public Date lastChangeTime;
    /**
     * 数据库路径，例如{@code ./data/database_name/}
     */
    public transient String databasePath;
    /**
     * 表文件夹路径，例如{@code ./data/database_name/table_name/}
     */
    public transient String directoryPath;
    /**
     * 这个表下的约束
     */
    private transient TableConstraintFactory constraintFactory;
    /**
     * 这个表下的定义
     */
    private transient TableDefineFactory defineFactory;
    /**
     * 这个表下的索引
     */
    private transient TableIndexFactory indexFactory;
    /**
     * 建表的SQL
     */
    public transient CreateTableParser parser;
    /**
     * 随机读写的工具
     */
    private transient RandomAccessFiles raf;

    @Deprecated
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

    public TableBlock(CreateTableParser parser, String databasePath) {
        this.parser = parser;
        this.databasePath = databasePath;
        directoryPath = databasePath + parser.getTableName() + "/";
        this.tableName = parser.getTableName();
        this.recordAmount = 0;
        this.fieldAmount = parser.getTableDefine().size();
        String temp = directoryPath + tableName;
        this.definePath = temp + ".tdf";
        this.constraintPath = temp + ".tic";
        this.recordPath = temp + ".trd";
        this.indexPath = temp + ".tix";
    }

    /**
     * 如果IndexFactory不存在，就反序列化后再放回
     *
     * @return IndexFactory
     * @see core.table.factory.TableComponentFactory#TableComponentFactory(TableBlock)
     */
    public TableIndexFactory getIndexFactory() {
        if (indexFactory == null)
            indexFactory = new TableIndexFactory(this);
        return indexFactory;
    }

    /**
     * 如果DefineFactory不存在，就反序列化后再放回
     *
     * @return DefineFactory
     * @see core.table.factory.TableComponentFactory#TableComponentFactory(TableBlock)
     */
    public TableDefineFactory getDefineFactory() {
        if (defineFactory == null)
            defineFactory = new TableDefineFactory(this);
        return defineFactory;
    }

    /**
     * 如果ConstraintFactory不存在，就反序列化后再放回
     *
     * @return ConstraintFactory
     * @see core.table.factory.TableComponentFactory#TableComponentFactory(TableBlock)
     */
    public TableConstraintFactory getConstraintFactory() {
        if (constraintFactory == null)
            constraintFactory = new TableConstraintFactory(this);
        return constraintFactory;
    }

    public RandomAccessFiles getRaf() {
        if (raf == null) {
            try {
                raf = new RandomAccessFiles(defineFactory.getCollection());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return raf;
    }

    /**
     * 创建当前表
     *
     * @return 创建结果
     */
    public Result create() {
        CreateUtil createUtil = CreateUtil.INSTANCE;
        return createUtil.createTable(this);
    }

    /**
     * 在表中插入数据
     *
     * @param parser 插入SQL解析器
     * @return 插入结果
     */
    public Result insert(InsertParser parser) {
        return InsertUtil.INSTANCE.insert(this, parser);
    }

    /**
     * 在表中创建索引
     *
     * @param parser 创建索引SQL解析器
     * @return 创建索引结果
     */
    public Result createIndex(CreateIndexParser parser) {
        Result result = getIndexFactory().createIndex(parser);
        return result;
    }
}

