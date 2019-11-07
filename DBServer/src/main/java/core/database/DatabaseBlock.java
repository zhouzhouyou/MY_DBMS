package core.database;

import core.file.Block;

import java.io.Serializable;
import java.util.Date;

class DatabaseBlock extends Block {


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
        this.path = "./" + name;
        this.createTime = new Date();
    }
}
