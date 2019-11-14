package core.database;

import core.table.factory.TableFactory;
import util.file.BlockCollections;
import util.file.FileUtils;
import util.file.exception.IllegalNameException;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static util.file.Path.DATABASE_PATH;

/**
 * 这个类是使用enum实现的单例。
 * {@link core.Core} 持有这个类的对象。
 * <p>这个类控制着{@link DatabaseCollection}的序列化和反序列化</p>
 * <p>这个类可以读写{@code .\/system.db}， 这个文件保存着这个数据中心下的所有数据库</p>
 *
 * @see DatabaseBlock
 * @see DatabaseCollection
 * @see BlockCollections
 */
public enum DatabaseFactory {
    INSTANCE;

    public static final boolean SYSTEM = true;
    public static final boolean USER = false;

    private DatabaseCollection collection;
    private Map<String, DatabaseBlock> map = new HashMap<>();

    /**
     * 初始化时，首先尝试从文件中反序列化{@link #collection}。
     * 如果成功了，就把所有数据写入{@link #map}。
     * 如果失败了，就意味着这数据中心是第一次被打开，于是创建一个新的对象并保存到文件，
     * 在应用程序关闭前，应当调用{@link #saveInstance()}}来把{@link #collection}序列化。
     *
     * @see DatabaseCollection#absolutePath
     */
    DatabaseFactory() {
        if (BlockCollections.exists(DATABASE_PATH)) {
            try {
                collection = (DatabaseCollection) BlockCollections.deserialize(DATABASE_PATH);
                collection.list.forEach(databaseBlock -> map.put(databaseBlock.name, databaseBlock));
            } catch (IOException | ClassNotFoundException | IllegalNameException e) {
                e.printStackTrace();
            }
        } else {
            collection = new DatabaseCollection();
        }
    }

    /**
     * 创建一个数据库。
     *
     * @param name 数据库名
     * @param type 数据库类型，参数为{@link #SYSTEM}或{@link #USER}
     * @return result
     */
    public Result createDatabase(String name, boolean type) {
        if (!FileUtils.isValidFileName(name)) return ResultFactory.buildInvalidNameResult(name);
        if (exists(name)) return ResultFactory.buildObjectAlreadyExistsResult();
        DatabaseBlock databaseBlock = new DatabaseBlock(name, type);
        collection.add(databaseBlock);
        map.put(name, databaseBlock);
        saveInstance();
        TableFactory tableFactory = new TableFactory(databaseBlock);
        tableFactory.saveInstance();
        return ResultFactory.buildSuccessResult(name);
    }

    /**
     * 检查数据库是否存在
     *
     * @param name 数据库名
     * @return 是否存在
     */
    public boolean exists(String name) {
        return map.containsKey(name);
    }

    /**
     * 释放对数据库的引用，以便于删除数据库
     * 只有有客户端持有对数据库的引用，这个数据库就不能被删除
     *
     * @param databaseName 需要释放的数据库
     * @see #dropDatabase(String)
     */
    public void releaseDatabase(String databaseName) {
        DatabaseBlock block = map.get(databaseName);
        block.release();
    }

    /**
     * 获取数据库的引用
     *
     * @param name 数据库名
     * @return 数据库
     * @throws Exception 数据库不存在
     */
    public DatabaseBlock getDatabase(String name) throws Exception {
        if (!exists(name)) throw new Exception(name + " not exists");
        DatabaseBlock databaseBlock = map.get(name);
        databaseBlock.request();
        return databaseBlock;
    }

    /**
     * 删除数据库
     *
     * @param name 数据库名
     * @return result
     */
    public Result dropDatabase(String name) {
        if (!exists(name)) return ResultFactory.buildObjectNotExistsResult();
        DatabaseBlock databaseBlock = map.get(name);
        if (!databaseBlock.free()) return ResultFactory.buildObjectOccupiedResult();
        map.values().removeIf(value -> value.equals(databaseBlock));
        collection.remove(databaseBlock);
        databaseBlock.delete();
        saveInstance();
        return ResultFactory.buildSuccessResult(name);
    }

    /**
     * 序列化至system.db.
     */
    public void saveInstance() {
        try {
            BlockCollections.serialize(collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
