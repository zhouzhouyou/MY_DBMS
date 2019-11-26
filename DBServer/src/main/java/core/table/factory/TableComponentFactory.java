package core.table.factory;

import core.table.block.TableBlock;
import core.table.collection.TableComponentCollection;
import util.file.Block;
import util.file.BlockCollections;
import util.file.exception.IllegalNameException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 表的组件的工厂
 *
 * @param <T> 表的组件
 * @param <V> 表的组件的集合
 */
public abstract class TableComponentFactory<T extends Block, V extends TableComponentCollection<T>> {
    /**
     * 表的名字
     */
    protected String tableName;
    /**
     * 表的路径，例如{@code .\/data\/database_name\/table_name\/}
     */
    protected String tablePath;
    /**
     * 集合的绝对路径，例如{@code .\/data\/database_name\/table_name\/table_name.xxx}
     */
    protected String absolutePath;
    /**
     * 表的组件的集合
     */
    protected V collection;
    /**
     * 防止表的组件被多次创建而使用的map
     * Key为组件的名字，Value为组件
     */
    protected Map<String, T> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    public TableComponentFactory(TableBlock tableBlock) {
        tableName = tableBlock.tableName;
        absolutePath = getAbsolutePath(tableBlock);
        tablePath = tableBlock.directoryPath;
        if (BlockCollections.exists(absolutePath)) {
            try {
                collection = (V) BlockCollections.deserialize(absolutePath);
                collection.list.forEach(tableComponent -> map.put(tableBlock.tableName, tableComponent));
            } catch (IOException | ClassNotFoundException | IllegalNameException e) {
                e.printStackTrace();
            }
        } else {
            collection = getInstance(tableBlock);
        }

    }

    /**
     * 得到组件的集合。
     *
     * @param tableBlock 组件集合属于哪一个tableBlock
     * @return 组件的集合
     */

    protected abstract V getInstance(TableBlock tableBlock);

    /**
     * 获取组件集合的绝对路径。
     *
     * @param tableBlock 组件集合属于哪一个tableBlock
     * @return 组件的集合的绝对路径
     */
    protected abstract String getAbsolutePath(TableBlock tableBlock);

    /**
     * @return 组件的集合的绝对路径
     */
    public String getAbsolutePath() {
        return collection.absolutePath;
    }

    /**
     * 检测组件是否存在。
     *
     * @param name 组件的标识
     * @return 组件是否存在
     */
    public boolean exists(String name) {
        return map.containsKey(name);
    }

    /**
     * 新增一个组件。
     *
     * @param name 组件名
     * @param t    组件
     */
    public void add(String name, T t) {
        collection.add(t);
        map.put(name, t);
        saveInstance();
    }

    /**
     * 获取一个组件。
     *
     * @param name 组件名
     * @return 组件
     */
    public T get(String name) {
        return map.get(name);
    }

    /**
     * 将当前文件序列化。
     */
    public void saveInstance() {
        try {
            BlockCollections.serialize(collection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新组件的集合
     */
    public void setCollection(V collection) {
        this.collection = collection;
    }

    /**
     * 获取组件的集合
     *
     * @return 组件的集合
     */

    public V getCollection() {
        return collection;
    }
}
