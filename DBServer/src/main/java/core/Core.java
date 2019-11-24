package core;

import core.database.DatabaseBlock;
import core.database.DatabaseFactory;
import core.table.factory.TableFactory;
import server.user.UserFactory;
import util.parser.parsers.*;
import util.result.Result;
import util.result.ResultFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
            return factory.createTable(parser);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
        }
    }

    @Deprecated
    public Result chooseDatabase(ChooseDatabaseParser parser) {
        try {
            DatabaseBlock block = databaseFactory.getDatabase(parser.getDatabaseName());
            return ResultFactory.buildSuccessResult(block);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult();
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
            return factory.dropTable(tableName);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
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
            return factory.insert(parser);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
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
            return factory.createIndex(parser);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
        }
    }

    public Result select(SelectParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            return factory.select(parser);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
        }
    }

    public Result update(UpdateParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            return factory.update(parser);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
        }
    }

    public Result delete(DeleteParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            return factory.delete(parser);
        } catch (Exception e) {
            return ResultFactory.buildObjectNotExistsResult(database);
        }
    }

    public Result alterTable(AlterTableParser parser, String database) {
        try {
            DatabaseBlock databaseBlock = databaseFactory.getDatabase(database);
            TableFactory factory = databaseBlock.getFactory();
            return factory.alterTable(parser);
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
        if (!parser.isValid()) return ResultFactory.buildFailResult(null);
        return userFactory.createUser(parser.getUserName(), parser.getPassword());
    }

    public Result dropUser(DropUserParser parser) {
        return userFactory.dropUser(parser.getUserName());
    }
}
