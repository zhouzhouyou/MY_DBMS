package core.database;

import core.file.BlockCollections;
import core.file.exception.IllegalNameException;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.*;

public enum DatabaseFactory {
    INSTANCE;

    public static final boolean SYSTEM = true;
    public static final boolean USER = false;

    private DatabaseCollection collection;
    private Map<String, DatabaseBlock>  map = new HashMap<>();

    DatabaseFactory() {
        if (BlockCollections.exists(DatabaseCollection.absolutePath)) {
            try {
                collection = (DatabaseCollection) BlockCollections.deserialize(DatabaseCollection.absolutePath);
                collection.list.forEach(databaseBlock -> map.put(databaseBlock.name, databaseBlock));
            } catch (IOException | ClassNotFoundException | IllegalNameException e) {
                e.printStackTrace();
            }
        } else {
            collection = new DatabaseCollection();
        }
    }

    /**
     * Create a Database.
     * @param name database's name
     * @param type type of database, using {@link #SYSTEM} or {@link #USER}
     * @return result
     */
    public Result createDatabase(String name, boolean type) {
        if (!BlockCollections.isValidFileName(name) || exists(name)) return ResultFactory.buildFailResult(name + " is invalid or used");
        DatabaseBlock databaseBlock = new DatabaseBlock(name, type);
        collection.add(databaseBlock);
        map.put(name, databaseBlock);
        return ResultFactory.buildSuccessResult(name);
    }

    /**
     * @return absolute path of the database file
     */
    public String getAbsolutePath() {
        return collection.getAbsolutePath();
    }

    /**
     * Check if the database exists
     * @param name database's name
     * @return true if exists
     */
    public boolean exists(String name) {
        return map.containsKey(name);
    }

    /**
     * Release database from the {@link #map} so it can be deleted.
     * Invoked by {@link DatabaseBlock#release()}
     * @param databaseBlock database to release
     */
    public void releaseDatabase(DatabaseBlock databaseBlock) {
        databaseBlock.release();
    }

    /**
     * Get the database from the map or file
     * @param name database's name
     * @return database or null
     * @throws Exception the database doesn't exists
     */
    public DatabaseBlock getDatabase(String name) throws Exception {
        if (!exists(name)) throw new Exception(name + " not exists");
        DatabaseBlock databaseBlock = map.get(name);
        databaseBlock.request();
        return databaseBlock;
    }

    /**
     * Drop the database
     * @param name database's name
     * @return result
     * @throws IOException the database file doesn't exists
     */
    public Result dropDatabase(String name) throws IOException {
        if (!exists(name)) throw new IOException(name + " not exists");
        DatabaseBlock databaseBlock = map.get(name);
        if (!databaseBlock.free()) return ResultFactory.buildFailResult("occupied");
        map.values().removeIf(value -> value.equals(databaseBlock));
        collection.remove(databaseBlock);
        //TODO remove relative files
        return ResultFactory.buildSuccessResult(name);
    }

    /**
     * Save current system.db
     */
    public void saveInstance() {
        try {
            BlockCollections.serialize(collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
