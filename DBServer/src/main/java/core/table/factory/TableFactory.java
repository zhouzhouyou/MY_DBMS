package core.table.factory;

import core.database.DatabaseBlock;
import core.table.block.TableBlock;
import core.table.collection.TableCollection;
import util.file.BlockCollections;
import util.file.exception.IllegalNameException;
import util.parser.parsers.CreateTableParser;
import util.parser.parsers.InsertParser;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableFactory {
    private TableCollection collection;
    private Map<String, TableBlock> map = new HashMap<>();
    private DatabaseBlock databaseBlock;

    public TableFactory(DatabaseBlock databaseBlock) {
        this.databaseBlock = databaseBlock;
        String absolutePath = databaseBlock.path + databaseBlock.name + "." + TableCollection.TABLE_POSTFIX;
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
    public Result createTable(String tableName, int recordAmount, int fieldAmount, String definePath, String constraintPath, String recordPath, String indexPath, Date createTime, Date lastChangeTime) {
        if (!BlockCollections.isValidFileName(tableName)) return ResultFactory.buildInvalidNameResult(tableName);
        if (exists(tableName)) return ResultFactory.buildObjectAlreadyExistsResult();
        TableBlock tableBlock = new TableBlock(tableName, recordAmount, fieldAmount, definePath, constraintPath, recordPath, indexPath, createTime, lastChangeTime);
        collection.add(tableBlock);
        map.put(tableName, tableBlock);
        return ResultFactory.buildSuccessResult(tableName);
    }

    /**
     * Get the table from map or file.
     *
     * @param tableName table's name
     * @return table or null
     * @throws Exception the table doesn't exists
     */

    public TableBlock getTable(String tableName) throws Exception {
        if (!exists(tableName)) throw new Exception(tableName + " not exists");
        TableBlock tableBlock = map.get(tableName);
        tableBlock.request();
        return tableBlock;
    }

    /**
     * Drop a table.
     *
     * @param tableName table's name
     * @return
     */

    public Result dropTable(String tableName) {
        if (!exists(tableName)) return ResultFactory.buildObjectNotExistsResult();
        TableBlock tableBlock = map.get(tableName);
        if (!tableBlock.free()) return ResultFactory.buildObjectOccupiedResult();
        map.values().removeIf(value -> value.equals(tableBlock));
        collection.remove(tableBlock);
        saveInstance();
        BlockCollections.delete(databaseBlock.path + tableName);
        //TODO remove relative files
        return ResultFactory.buildSuccessResult(tableName);
    }

    /**
     * @return absolute path of the DatabaseName.tb file
     */
    public String getAbsolutePath() {
        return collection.absolutePath;
    }

    /**
     * Check if the table exists.
     *
     * @param tableName name of the table
     * @return true if exists
     */
    public boolean exists(String tableName) {
        return map.containsKey(tableName);
    }

    /**
     * Release table from the{@link #map} so it can be deleted.
     * Invoked by{@link TableBlock#release()}.
     *
     * @param tableBlock table to release
     */
    public void releaseTable(TableBlock tableBlock) {
        tableBlock.release();
    }

    /**
     * Save current DatabaseName.tb.
     */
    public void saveInstance() {
        try {
            BlockCollections.serialize(collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Result createTable(CreateTableParser parser) {
        String tableName = parser.getTableName();
        if (!BlockCollections.isValidFileName(tableName)) return ResultFactory.buildInvalidNameResult(tableName);
        if (exists(tableName)) return ResultFactory.buildObjectAlreadyExistsResult();
        TableBlock tableBlock = new TableBlock(parser, databaseBlock.path);
        Result result = tableBlock.create();
        if (result.code == ResultFactory.SUCCESS) {
            collection.add(tableBlock);
            map.put(tableName, tableBlock);
            saveInstance();
        }
        return result;
    }

    public void delete() {
        if (databaseBlock != null) BlockCollections.delete(databaseBlock.path);
    }

    public Result insert(InsertParser parser) {
        String tableName = parser.getTableName();
        if (!exists(tableName)) return ResultFactory.buildObjectNotExistsResult();
        return map.get(tableName).insert(parser);
    }
}
