package core.table.factory;

import core.database.DatabaseBlock;
import core.table.block.TableBlock;
import core.table.collection.TableCollection;
import util.file.BlockCollections;
import util.file.FileUtils;
import util.file.exception.IllegalNameException;
import util.parser.parsers.*;
import util.result.Result;
import util.result.ResultFactory;
import util.table.SelectUtil;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对一个数据库下的Table进行操作的工厂
 */
public class TableFactory {
    /**
     * 存储这所有Table的集合
     */
    private TableCollection collection;
    /**
     * 为了防止Table被多次实例化
     */
    private Map<String, TableBlock> map = new HashMap<>();
    /**
     * 数据库地址，例如{@code .\/data\/database_name\/}
     */
    private String databasePath;


    public TableFactory(DatabaseBlock databaseBlock) {
        databasePath = databaseBlock.path;
        String absolutePath = databasePath + databaseBlock.name + "." + TableCollection.TABLE_POSTFIX;
        if (BlockCollections.exists(absolutePath)) {
            try {
                collection = (TableCollection) BlockCollections.deserialize(absolutePath);
                collection.list.forEach(tableBlock -> map.put(tableBlock.tableName, tableBlock));
            } catch (IllegalNameException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            collection = new TableCollection(absolutePath);
        }
    }

    /**
     * Create a table.
     *
     * @param tableName      table's name
     * @param recordAmount   amount of records in this table
     * @param fieldAmount    amount of fields in table definition
     * @param definePath     absolute path of the definition file of this table
     * @param constraintPath absolute path of the constraint file of this table
     * @param recordPath     absolute path of the data storage file of this table
     * @param indexPath      absolute path of the index information of this table
     * @param createTime     time this table was created
     * @param lastChangeTime time of the latest change of this table
     * @return result
     */
    @Deprecated
    public Result createTable(String tableName, int recordAmount, int fieldAmount, String definePath, String constraintPath, String recordPath, String indexPath, Date createTime, Date lastChangeTime) {
        if (!FileUtils.isValidFileName(tableName)) return ResultFactory.buildInvalidNameResult(tableName);
        if (exists(tableName)) return ResultFactory.buildObjectAlreadyExistsResult();
        TableBlock tableBlock = new TableBlock(tableName, recordAmount, fieldAmount, definePath, constraintPath, recordPath, indexPath, createTime, lastChangeTime);
        collection.add(tableBlock);
        map.put(tableName, tableBlock);
        return ResultFactory.buildSuccessResult(tableName);
    }

    /**
     * 从map中获取table，并且让table的计数加一（释放后才能删除表）。
     *
     * @param tableName 表名
     * @return 表
     * @throws Exception 表不存在
     */

    public TableBlock getTable(String tableName) throws Exception {
        if (!exists(tableName)) throw new Exception(tableName + " not exists");
        TableBlock tableBlock = map.get(tableName);
        tableBlock.request();
        return tableBlock;
    }

    public void releaseTable(String tableName) {
        if (!exists(tableName)) return;
        TableBlock tableBlock = map.get(tableName);
        tableBlock.release();
    }


    /**
     * 删除表。
     *
     * @param tableName 表名
     * @return 如果不存在这个表，返回{@link ResultFactory#NOT_FOUND}, 如果表被占用了，返回{@link ResultFactory#CONFLICT}
     */
    public Result dropTable(String tableName) {
        if (!exists(tableName)) return ResultFactory.buildObjectNotExistsResult();
        TableBlock tableBlock = map.get(tableName);
        if (!tableBlock.free()) return ResultFactory.buildObjectOccupiedResult();
        map.values().removeIf(value -> value.equals(tableBlock));
        collection.remove(tableBlock);
        saveInstance();
        FileUtils.delete(databasePath + tableName);
        return ResultFactory.buildSuccessResult(tableName);
    }

    /**
     * @return DatabaseName.tb的绝对路径
     */
    public String getAbsolutePath() {
        return collection.absolutePath;
    }

    /**
     * 检测表是否存在。
     *
     * @param tableName 表名
     * @return 表是否存在
     */
    public boolean exists(String tableName) {
        return map.containsKey(tableName);
    }

    /**
     * 从{@link #map}中释放对表的占用。
     * 被{@link TableBlock#release()}调用。
     *
     * @param tableBlock 需要释放的表
     */
    public void releaseTable(TableBlock tableBlock) {
        tableBlock.release();
    }

    /**
     * 实例化表的集合至DatabaseName.tb。
     */
    public void saveInstance() {
        try {
            BlockCollections.serialize(collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建表
     *
     * @param parser 创建表SQL解析器
     * @return 创建表的结果
     */
    public Result createTable(CreateTableParser parser) {
        String tableName = parser.getTableName();
        if (!FileUtils.isValidFileName(tableName)) return ResultFactory.buildInvalidNameResult(tableName);
        if (exists(tableName)) return ResultFactory.buildObjectAlreadyExistsResult();
        TableBlock tableBlock = new TableBlock(parser, databasePath);
        Result result = tableBlock.create();
        if (result.code == ResultFactory.SUCCESS) {
            collection.add(tableBlock);
            map.put(tableName, tableBlock);
            saveInstance();
        }
        return result;
    }

    /**
     * 插入一条记录
     *
     * @param parser 插入SQL语法解析器
     * @return 插入结果
     */
    public Result insert(InsertParser parser) {
        String tableName = parser.getTableName();
        if (!exists(tableName)) return ResultFactory.buildObjectNotExistsResult(tableName);
        return map.get(tableName).insert(parser);
    }

    /**
     * 创建一个索引
     *
     * @param parser 创建索引SQL语法解析器
     * @return 创建索引结果
     */
    public Result createIndex(CreateIndexParser parser) {
        String tableName = parser.getTableName();
        if (!exists(tableName)) return ResultFactory.buildObjectNotExistsResult(tableName);
        return map.get(tableName).createIndex(parser);
    }

    public Result select(SelectParser parser) {
        try {
            SelectUtil selectUtil = new SelectUtil(this, parser);
            return selectUtil.select();
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    public Result update(UpdateParser parser) {
        String tableName = parser.getTableName();
        if (!exists(tableName)) return ResultFactory.buildObjectNotExistsResult(tableName);
        return map.get(tableName).update(parser);
    }

    public Result delete(DeleteParser parser) {
        String tableName = parser.getTableName();
        if (!exists(tableName)) return ResultFactory.buildObjectNotExistsResult(tableName);
        return map.get(tableName).delete(parser);
    }
}
