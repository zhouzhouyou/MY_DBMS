package core.database;

import core.file.BlockCollections;
import core.file.exception.IllegalNameException;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.*;

/**
 * The {@code DatabaseFactory} class is a singleton class created by enum method.
 * {@link core.Core} hold the instance of this.
 * <p>This class can control the serialization and the deserialization of the {@link DatabaseCollection}.</p>
 * <p>This factory read and write file {@code "./system.db"}, which save how many databases are there in the system.</p>
 * <p>This factory control the create and drop actions of database as well.</p>
 * @see DatabaseBlock
 * @see DatabaseCollection
 * @see BlockCollections
 */
public enum DatabaseFactory {
    INSTANCE;

    public static final boolean SYSTEM = true;
    public static final boolean USER = false;

    private DatabaseCollection collection;
    private Map<String, DatabaseBlock>  map = new HashMap<>();

    /**
     * When initializing the {@link #INSTANCE}, try to deserialize object from the "system.db" at first.
     * If succeeded, populate all database blocks into {@link #map}.
     * If failed, it means that the system is launched at the first time, then we create a new instance.
     * Before the application being closed,
     * should invoke {@link #saveInstance()}} to serialize the {@link #collection} to "system.db".
     * @see DatabaseCollection#absolutePath
     */
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
     * Create a Database. Return the result whether the factory create a <b>new</b> database
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
     * Check if the database exists.
     * @param name database's name
     * @return true if exists
     */
    public boolean exists(String name) {
        return map.containsKey(name);
    }

    /**
     * Release database so it can be deleted.
     * @param databaseBlock database to release
     */
    public void releaseDatabase(DatabaseBlock databaseBlock) {
        databaseBlock.release();
    }

    /**
     * Get the database from the map.
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
     * Drop the database.
     * @param name database's name
     * @return result
     * @throws IOException the database file doesn't exists
     */
    public Result dropDatabase(String name)  {
        if (!exists(name)) return ResultFactory.buildFailResult("not exists");
        DatabaseBlock databaseBlock = map.get(name);
        if (!databaseBlock.free()) return ResultFactory.buildFailResult("occupied");
        map.values().removeIf(value -> value.equals(databaseBlock));
        collection.remove(databaseBlock);
        //TODO remove relative files
        return ResultFactory.buildSuccessResult(name);
    }

    /**
     * Save current system.db.
     */
    public void saveInstance() {
        try {
            BlockCollections.serialize(collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
