package core;

import core.database.DatabaseFactory;
import util.result.Result;

public class Core {
    private DatabaseFactory databaseFactory = DatabaseFactory.INSTANCE;

    /**
     * Create a database.
     * @param name name of the database
     * @param type type of the database, use {@link DatabaseFactory#SYSTEM} or {@link DatabaseFactory#USER}
     * @return result
     */
    public Result createDatabase(String name, boolean type) {
        return databaseFactory.createDatabase(name, type);
    }

    public Result dropDatabase(String name) {
        return databaseFactory.dropDatabase(name);
    }
}
