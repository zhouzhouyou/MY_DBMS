package core.database;


import core.table.factory.TableFactory;
import util.file.Block;
import util.file.BlockCollections;

import java.util.Date;

public class DatabaseBlock extends Block {

    private transient TableFactory factory;
    public String name;
    public boolean type;
    public String path;
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

    public TableFactory getFactory() {
        if (factory == null)
            factory = new TableFactory(this);
        return factory;
    }

    public void delete() {
        BlockCollections.delete("./data/" + name);
    }
}
