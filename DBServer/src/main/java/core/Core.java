package core;

import core.database.DatabaseBlock;
import core.database.DatabaseFactory;
import core.table.block.TableBlock;
import core.table.factory.TableFactory;
import server.user.UserFactory;
import util.parser.parsers.*;
import util.result.Result;
import util.result.ResultFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 分发{@link server.ClientHandler}中的事件。
 * 请求和基础功能调用之间的耦合层。
 */
public enum Core {
    INSTANCE;

    private DatabaseFactory databaseFactory = DatabaseFactory.INSTANCE;
    private UserFactory userFactory = UserFactory.INSTANCE;

    private Map<String, TableFactory> tableFactoryMap = new HashMap<>();

    /**
     * 创建一个数据库。
     *
     * @param name 数据库名
     * @param type 数据库类型，使用{@link DatabaseFactory#SYSTEM} 或 {@link DatabaseFactory#USER}
     * @return 创建数据库结果
     */
    public Result createDatabase(String name, boolean type) {
        Result result = databaseFactory.createDatabase(name, type);
        //saveDatabase();
        return result;
    }

    /**
     * 删除一个数据库。
     *
     * @param name 数据库名
     * @return 删除数据库的结果
     */
    public Result dropDatabase(String name) {
        Result result = databaseFactory.dropDatabase(name);
        //saveDatabase();
        return result;
    }

    /**
     * 获取一个用户是否拥有一个权限。
     *
     * @param user      用户名
     * @param grantType 权限类型
     * @return 用户是否拥有该权限
     */
    public Result getGrant(String user, String grantType) {
        return userFactory.getGrant(user, grantType);
    }

    /**
     * 用户登录。
     *
     * @param user     用户名
     * @param password 用户密码
     * @return 登录是否有效
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

    /**
     * 创建一个表
     *
     * @param parser   创建表SQL解析器
     * @param database 数据库名
     * @return 创建表的结果
     */
    public Result createTable(CreateTableParser parser, String database) {
        try {
            DatabaseBlock block = databaseFactory.getDatabase(database);
            TableFactory factory = block.getFactory();
            Result result = factory.createTable(parser);
            block.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    @Deprecated
    public Result chooseDatabase(ChooseDatabaseParser parser) {
        try {
            DatabaseBlock block = databaseFactory.getDatabase(parser.getDatabaseName());
            return ResultFactory.buildSuccessResult(block);
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    /**
     * 删除一张表
     *
     * @param tableName 删除的表名
     * @param database  数据库名
     * @return 删除表的结果
     */
    public Result dropTable(String tableName, String database) {
        try {
            DatabaseBlock block = databaseFactory.getDatabase(database);
            TableFactory factory = block.getFactory();
            Result result = factory.dropTable(tableName);
            block.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    /**
     * 插入一条记录
     *
     * @param parser   插入记录SQL解析器
     * @param database 数据库名
     * @return 插入记录的结果
     */
    public Result insert(InsertParser parser, String database) {
        try {
            DatabaseBlock block = databaseFactory.getDatabase(database);
            TableFactory factory = block.getFactory();
            Result result = factory.insert(parser);
            block.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }

    }

    /**
     * 释放数据库
     *
     * @param database 数据库名
     * @return 释放数据库的结果
     */
    public Result releaseDatabase(String database) {
        databaseFactory.releaseDatabase(database);
        return ResultFactory.buildSuccessResult(null);
    }

    /**
     * 创建一个索引
     *
     * @param parser   创建索引SQL解析器
     * @param database 数据库名
     * @return 创建索引的结果
     */
    public Result createIndex(CreateIndexParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.createIndex(parser);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    public Result select(SelectParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.select(parser);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
        }
    }

    public Result update(UpdateParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.update(parser);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
        }
    }

    public Result delete(DeleteParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.delete(parser);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    public Result alterTable(AlterTableParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.alterTable(parser);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult();
        }
    }

    public Result getDatabases() {
        return ResultFactory.buildSuccessResult(databaseFactory.getDatabaseNames());
    }

    public Result grant(GrantParser parser, String source) {
        return userFactory.grant(source, parser.getUserName(), parser.getGrant(), parser.isGrant());
    }

    public Result createUser(CreateUserParser parser) {
        if (!parser.isValid()) return ResultFactory.buildFailResult("invalid");
        return userFactory.createUser(parser.getUserName(), parser.getPassword());
    }

    public Result dropUser(DropUserParser parser) {
        return userFactory.dropUser(parser.getUserName());
    }

    public Result getTables(GetTables parser) {
        String databaseName = parser.getDatabaseName();
        if (!databaseFactory.exists(databaseName)) return ResultFactory.buildObjectNotExistsResult(databaseName);
        return databaseFactory.getTables(databaseName);
    }

    public Result getTableDefine(String tableName, String databaseName) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(databaseName);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.getTableDefine(tableName);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    public Result getTableConstraint(String tableName, String databaseName) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(databaseName);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.getTableConstraint(tableName);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    public Result getTableIndex(String tableName, String databaseName) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(databaseName);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.getTableIndex(tableName);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }

    public Result dropIndex(DropIndexParser parser, String databaseName) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(databaseName);
            TableFactory factory = databaseBlock.getFactory();
            Result result = factory.dropIndex(parser);
            databaseBlock.release();
            return result;
        } catch (Exception e) {
            return ResultFactory.buildFailResult(e.toString());
        }
    }
}
