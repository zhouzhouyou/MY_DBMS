package core.database;


import core.table.factory.TableFactory;
import util.file.Block;
import util.file.FileUtils;

import java.util.Date;

/**
 * 这个类实践上保管了一个Database，作为DatabaseCollection的list中的一个元素存在。
 * DatabaseCollection序列化时，这个类也会序列化。
 */
public class DatabaseBlock extends Block {
    /**
     * 这个数据库下的{@link TableFactory}
     */
    private transient TableFactory factory;
    /**
     * 数据库名
     */
    public String name;
    /**
     * 数据库类型，有系统和用户两种
     *
     * @see DatabaseFactory#SYSTEM
     * @see DatabaseFactory#USER
     */
    public boolean type;
    /**
     * 数据库的路径
     */
    public String path;
    /**
     * 创建时间，永远是{@code new Date()}
     */
    public Date createTime;

    public DatabaseBlock(String name) {
        new DatabaseBlock(name, false);
    }

    public DatabaseBlock(String name, boolean type) {
        this.name = name;
        this.type = type;
        this.path = "./data/" + name + "/";
        this.createTime = new Date();
    }

    /**
     * 因为每个DatabaseBlock只有一个实例，这里也保证了每个TableFactory也只有一个实例
     *
     * @return
     */
    public TableFactory getFactory() {
        if (factory == null)
            factory = new TableFactory(this);
        return factory;
    }

    public void delete() {
        FileUtils.delete("./data/" + name);
        factory = null;
    }
}
