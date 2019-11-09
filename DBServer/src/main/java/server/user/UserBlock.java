package server.user;

import util.file.Block;

public class UserBlock extends Block {
    public String name;
    public String password;
    public boolean createTable = false;
    public boolean dropTable = false;
    public boolean createDatabase = false;
    public boolean dropDatabase = false;
    public boolean grant = false;

    public UserBlock(String name, String password) {
        this.name = name;
        this.password = password;
        if (name.equals("system")) {
            createDatabase = true;
            dropDatabase = true;
            createTable = true;
            dropTable = true;
            grant = true;
        }
    }
}
