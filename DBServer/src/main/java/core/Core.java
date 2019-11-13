package core;

import core.database.DatabaseBlock;
import core.database.DatabaseFactory;
import core.table.factory.TableFactory;
import server.user.UserFactory;
import util.parser.parsers.ChooseDatabaseParser;
import util.parser.parsers.CreateIndexParser;
import util.parser.parsers.CreateTableParser;
import util.parser.parsers.InsertParser;
import util.result.Result;
import util.result.ResultFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Core is meant to handle {@link server.ClientHandler}.
 * It's a layer between client and basic functions.
 */
public enum Core {
    INSTANCE;

    private DatabaseFactory databaseFactory = DatabaseFactory.INSTANCE;
    private UserFactory userFactory = UserFactory.INSTANCE;

    private Map<String, TableFactory> tableFactoryMap = new HashMap<>();

    /**
     * Create a database.
     *
     * @param name name of the database
     * @param type type of the database, use {@link DatabaseFactory#SYSTEM} or {@link DatabaseFactory#USER}
     * @return result
     */
    public Result createDatabase(String name, boolean type) {
        Result result = databaseFactory.createDatabase(name, type);
        //saveDatabase();
        return result;
    }

    /**
     * Drop a database.
     *
     * @param name name of the database
     * @return result whether the database dropped
     */
    public Result dropDatabase(String name) {
        Result result = databaseFactory.dropDatabase(name);
        //saveDatabase();
        return result;
    }

    /**
     * Get grant of a certain grant type of a user.
     *
     * @param user      user
     * @param grantType grant type
     * @return result whether user has the grant
     */
    public Result getGrant(String user, String grantType) {
        return userFactory.getGrant(user, grantType);
    }

    /**
     * Connect user to system.
     *
     * @param user     username
     * @param password password
     * @return result whether the connect is valid
     */
    public Result connect(String user, String password) {
        return userFactory.connect(user, password);
    }

    /**
     * Serialize the database collection to "system.db".
     */
    public void saveDatabase() {
        databaseFactory.saveInstance();
    }

    /**
     * Serialize the user collection to "system.user".
     */
    public void saveUser() {
        userFactory.saveInstance();
    }

    public Result createTable(CreateTableParser parser, DatabaseBlock database) {
        TableFactory factory = database.getFactory();
        return factory.createTable(parser);
    }

    public Result chooseDatabase(ChooseDatabaseParser parser) {
        try {
            DatabaseBlock block = databaseFactory.getDatabase(parser.getDatabaseName());
            return ResultFactory.buildSuccessResult(block);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult();
        }
    }

    public Result dropTable(String tableName, DatabaseBlock database) {
        TableFactory factory = database.getFactory();
        return factory.dropTable(tableName);
    }

    public Result insert(InsertParser parser, DatabaseBlock database) {
        TableFactory factory = database.getFactory();
        return factory.insert(parser);
    }

    public Result releaseDatabase(DatabaseBlock database) {
        databaseFactory.releaseDatabase(database);
        return ResultFactory.buildSuccessResult(null);
    }

    public Result createIndex(CreateIndexParser parser, DatabaseBlock database) {
        TableFactory factory = database.getFactory();
        return factory.createIndex(parser);
    }
}
