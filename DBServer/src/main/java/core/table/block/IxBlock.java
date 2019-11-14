package core.table.block;

import util.file.Block;
import util.file.FileUtils;

import java.io.IOException;
import java.util.List;

//TODO: 这个是实现你数据结构的地方，Block实现了序列化接口，可以调用SerializeFiles.serialize(absolutePath)

/**
 * 这个类基于特定的数据结构（B+树），这个文件会在需要的时候从文件反序列化。
 * <p>
 * 在本文档中出现的所有索引都指的是在trd文件中，该条数据是第几个。
 * </p>
 */
public class IxBlock extends Block {
    //如果你要声明一个不应该被序列化的属性，请加上transient关键字，例如: private transient int age;
    // TODO: 写一个可序列化的结构，让反序列化时也能知道每个Comparable中的数据

    /**
     * 这个索引是否有unique属性。一般来说，primary Comparable对应的属性和unique属性都会自动建立一个unique的索引。
     */
    private boolean unique;
    /**
     * 索引名，也就是域名，最后这个类会保存在name.ix
     */
    private String name;
    /**
     * IndexBlock
     */
    private transient IndexBlock indexBlock;

    public IxBlock(IndexBlock indexBlock) {
        this.indexBlock = indexBlock;
    }

    /**
     * 返回与某个具体值相等或不等的索引的集合。
     * <pre>
     *     get(5, true)将会得到所有key = 5的值的索引的集合，
     *     get(6, false)将会得到所有key != 6的值的索引的集合。
     * </pre>
     *
     * @param key   值
     * @param equal 是否等于这个值
     * @return 与某个具体值相等或不等的索引的集合
     */
    public List<Integer> get(Comparable key, boolean equal) {
        //TODO: 返回某个具体值相等或不等的索引的集合
        return null;
    }

    /**
     * 返回小于某个范围的值的索引的集合。
     *
     * @param maxKey    最大值
     * @param exclusive 是否去除最大值
     * @return 返回小于某个范围的值的索引的集合
     */
    public List<Integer> lower(Comparable maxKey, boolean exclusive) {
        //TODO: 返回小于（等于）索引，这取决于是否为exclusive
        return null;
    }

    /**
     * 返回大于某个范围的值的索引的集合。
     *
     * @param minKey    最小值
     * @param exclusive 是否去除最小值
     * @return 返回大于某个范围的值的索引的集合
     */
    public List<Integer> larger(Comparable minKey, boolean exclusive) {
        //TODO: 返回大于（等于）这个值的索引，这取决于是否为exclusive
        return null;
    }

    /**
     * 返回两个Comparable之间的值的索引的集合（包含边界）。
     *
     * @param minKey 最小值
     * @param maxKey 最大值
     * @return 两个Comparable之间的值的索引的集合
     */
    @SuppressWarnings("unchecked")
    public List<Integer> between(Comparable minKey, Comparable maxKey) {
        //TODO: 两个Comparable之间的值的索引的集合（包含边界）
        if (maxKey.compareTo(minKey) < 0) return between(maxKey, minKey);
        return null;
    }

    /**
     * 返回两个Comparable之外的值的索引的集合（不包含边界）。
     *
     * @param minKey 最小值
     * @param maxKey 最大值
     * @return 两个Comparable之外的值的索引的集合
     */
    @SuppressWarnings("unchecked")
    public List<Integer> notBetween(Comparable minKey, Comparable maxKey) {
        //TODO: 不在两个Comparable之间的值的索引的集合
        if (maxKey.compareTo(minKey) < 0) return notBetween(maxKey, minKey);
        return null;
    }

    /**
     * 返回存在于list中的值的索引的集合。
     * <p>
     * 例如，select * from student where age in (5, 18, 19, 100)，需要返回5岁，18岁，19岁和100岁的学生的索引
     * </p>
     *
     * @param list 值的集合
     * @return 存在于list中的值的索引的集合
     */
    public List<Integer> in(List<Comparable> list) {
        //TODO: 存在于list中的值的索引的集合
        return null;
    }

    /**
     * 返回不存在于list中的值的索引的集合。
     * <p>
     * 例如，select * from student where age not in (8, 19)，需要返回不是18，19岁的学生的索引
     * </p>
     *
     * @param list 值的集合
     * @return 不存在于list中的值的索引的集合
     */
    public List<Integer> notIn(List<Comparable> list) {
        //TODO: 不存在于list中的值的索引的集合
        return null;
    }

    /**
     * 插入一条数据进入索引
     *
     * @param Comparable 数据
     * @param index      数据的索引
     * @return 插入是否成功（尤其是对于Unique索引而言）
     */
    public boolean insert(Comparable Comparable, int index) {
        //TODO: 插入一条数据进入索引
        return false;
    }

    /**
     * 将所有数据插入索引
     *
     * @param list 所有数据
     * @return 插入是否成功
     */
    public boolean insert(List<Comparable> list) {
        //TODO: 插入所有数据进入索引，这发生事后建立索引的情况下。
        return false;
    }

    /**
     * 删除一组索引（这是因为表中删除了一组记录，那么也需要删除对应的索引）
     *
     * @param index 索引
     * @return 删除是否成功
     */
    public boolean delete(List<Integer> index) {
        //TODO: 删除一组索引
        return false;
    }

    /**
     * 更新了一条数据后，移动原有索引的位置
     *
     * @param index  索引
     * @param newKey 新数据
     * @return 更新是否成功
     */
    public boolean update(int index, Comparable newKey) {
        //TODO: 更新了一条数据，需要移动索引的位置
        return false;
    }

    public void saveInstance() {
        try {
            FileUtils.serialize(this, indexBlock.indexFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
