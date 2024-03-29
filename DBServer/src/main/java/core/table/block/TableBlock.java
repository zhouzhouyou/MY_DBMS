package core.table.block;

import com.google.gson.Gson;
import core.table.collection.TableDefineCollection;
import core.table.factory.TableConstraintFactory;
import core.table.factory.TableDefineFactory;
import core.table.factory.TableIndexFactory;
import util.entity.Define;
import util.file.Block;
import util.file.RandomAccessFiles;
import util.file.exception.IllegalNameException;
import util.parser.parsers.*;
import util.result.Result;
import util.result.ResultFactory;
import util.table.*;

import java.io.IOException;
import java.util.*;

import static util.table.FieldTypes.*;

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
    public String directoryPath;
    /**
     * 建表的SQL
     */
    public transient CreateTableParser parser;
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

    /**
     * 返回随机访问工具
     *
     * @return 这个table的随机访问工具
     */
    public RandomAccessFiles getRaf() {
        if (raf == null) {
            try {
                raf = new RandomAccessFiles(getDefineFactory().getCollection());
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
        return CreateUtil.INSTANCE.createTable(this);
    }

    /**
     * 在表中插入数据
     *
     * @param parser 插入SQL解析器
     * @return 插入结果
     */
    public Result insert(InsertParser parser) {
        return InsertUtil.insert(this, parser);
    }

    /**
     * 在表中创建索引
     *
     * @param parser 创建索引SQL解析器
     * @return 创建索引结果
     */
    public Result createIndex(CreateIndexParser parser) {
        return getIndexFactory().createIndex(parser);
    }

    public Result update(UpdateParser parser) {
        return UpdateUtil.update(this, parser);
    }

    public Result delete(DeleteParser parser) {
        return DeleteUtil.delete(this, parser);
    }

    public Result alterTable(AlterTableParser parser) throws IllegalNameException, IOException, ClassNotFoundException {
        return AlterUtil.INSTANCE.alterTable(this, parser);
    }

    public boolean hasField(String fieldName) {
        return getDefineFactory().getCollection().hasField(fieldName);
    }

    public Result getTableDefine() {
        List<List<Object>> defines = new ArrayList<>();
        for (DefineBlock defineBlock : getDefineFactory().getCollection().list) {
            String fieldName = defineBlock.fieldName;
            String fieldType = FieldTypes.getFieldType(defineBlock.fieldType);
            if (defineBlock.fieldType == VARCHAR) fieldType += "(" + defineBlock.param + ")";
            boolean pk = false;
            boolean notNull = false;
            boolean unique = false;
            String check = null;
            Object defaultValue = null;
            for (ConstraintBlock constraintBlock : getConstraintFactory().getCollection().list) {
                if (!constraintBlock.fieldName.equals(defineBlock.fieldName)) continue;
                switch (constraintBlock.constraintType) {
                    case PK:
                        pk = true;
                        break;
                    case NOT_NULL:
                        notNull = true;
                        break;
                    case UNIQUE:
                        unique = true;
                        break;
                    case CHECK:
                        check = (String) constraintBlock.param;
                        break;
                    case DEFAULT:
                        defaultValue = constraintBlock.param;
                }
            }
            List<Object> definePropertyList = new ArrayList<>();
            definePropertyList.add(fieldName);
            definePropertyList.add(fieldType);
            definePropertyList.add(pk);
            definePropertyList.add(notNull);
            definePropertyList.add(unique);
            definePropertyList.add(check);
            definePropertyList.add(defaultValue);
            //Define define = new Define(fieldName, fieldType, pk, notNull, unique, check, defaultValue);
            defines.add(definePropertyList);
        }

        return ResultFactory.buildSuccessResult(defines);
    }

    public Result getTableConstraint() {
        List<List<Object>> lists = new ArrayList<>();
        for (ConstraintBlock constraintBlock : getConstraintFactory().getCollection().list) {
            lists.add(constraintBlock.getInfo());
        }
        return ResultFactory.buildSuccessResult(lists);
    }

    public Result getTableIndex() {
        List<List<Object>> lists = new ArrayList<>();
        for(IndexBlock indexBlock : getIndexFactory().getCollection().list){
            lists.add(indexBlock.getInfo());
        }
        return ResultFactory.buildSuccessResult(lists);
    }

    public Result dropIndex(String indexName) {
        return getIndexFactory().dropIndex(indexName);
    }
}

