package core.database;

import core.file.SimpleFileFactory;
import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;
import util.result.DefaultResult;
import util.result.Result;
import util.result.ResultCode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public enum DatabaseFactory {
    INSTANCE;

    private static final String path = "./";
    private static final String postfix = ".db";
    private Map<String, Database>  map = new HashMap<>();
    private List<Database> occupied = new ArrayList<>();

    private SimpleFileFactory<Database> simpleFileFactory = new SimpleFileFactory<>();

    /**
     * Create a Database.
     * @param name database's name
     * @param type type of database, using {@link Database#SYSTEM} or {@link Database#USER}
     * @return {@link DefaultResult} for exception, {@link Result} for normal case
     */
    public Result createDatabase(String name, boolean type) {
        if (exists(name)) return new DefaultResult();
        try {
            Database database = new Database(name, type, new Date());
            database.writeFile();
            return new Result(ResultCode.SUCCESS, database.filename);
        } catch (EmptyNameException | IllegalNameException | IOException e) {
            return new DefaultResult(e.toString());
        }
    }

    /**
     * Get the absolute path based on the {@link Database#DB_PATH} and {@link Database#DB_POST_FIX}.
     * @param name database's name
     * @return absolute path of the database file
     */
    public String getAbsolutePath(String name) {
        return Database.DB_PATH + name + "." + Database.DB_POST_FIX;
    }

    /**
     * Check if the database file exists in the root directory
     * @param name database's name
     * @return true if exists
     */
    public boolean exists(String name) {
        return new File(getAbsolutePath(name)).exists();
    }

    /**
     * Release database from the {@link #occupied} so it can be deleted.
     * Invoked by {@link Database#release()}
     * @param database database to release
     */
    public void releaseDatabase(Database database) {
        Iterator<Database> iterator = occupied.iterator();
        while (iterator.hasNext()) {
            Database current = iterator.next();
            if (current.equals(database)) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Get the database from the map or file
     * @param name database's name
     * @return database or null
     * @throws IOException the database file doesn't exists
     * @throws ClassNotFoundException serialVersionUID changed
     * @throws IllegalNameException name is illegal
     */
    public Database getDatabase(String name) throws IOException, ClassNotFoundException, IllegalNameException {
        if (!exists(name)) throw new IOException(name + " not exists");
        Database database;
        if (map.containsKey(name)) database = map.get(name);
        else {
            database = simpleFileFactory.build(getAbsolutePath(name));
            map.put(name, database);
        }
        occupied.add(database);
        return database;
    }

    /**
     * Drop the database
     * @param name database's name
     * @return {@link DefaultResult} for exception
     * @throws IOException the database file doesn't exists
     * @throws ClassNotFoundException serialVersionUID changed
     * @throws IllegalNameException name is illegal
     */
    public Result dropDatabase(String name) throws IOException, ClassNotFoundException, IllegalNameException {
        if (!exists(name)) throw new IOException(name + " not exists");
        Database database = simpleFileFactory.build(getAbsolutePath(name));
        if (occupied.contains(database)) return new DefaultResult("occupied");
        map.values().removeIf(value -> value.equals(database));
        File file = new File(getAbsolutePath(name));
        if (file.delete()) {
            //TODO delete records
            return new Result(ResultCode.SUCCESS, name);
        } else {
            return new DefaultResult("fail to delete");
        }

    }
}
