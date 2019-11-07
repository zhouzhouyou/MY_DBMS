package core;

import core.database.Database;
import core.database.DatabaseFactory;
import util.result.Result;

public class Core {
    private DatabaseFactory databaseFactory = DatabaseFactory.INSTANCE;

    /**
     * Create a database.
     * @param name name of the database
     * @param type type of the database, use {@link Database#SYSTEM} or {@link Database#USER}
     * @return {@link util.result.DefaultResult} if failed
     */
    public Result createDatabase(String name, boolean type) {
        return databaseFactory.createDatabase(name, type);
    }


}
