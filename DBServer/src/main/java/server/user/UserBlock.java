package server.user;

import util.file.Block;

public class UserBlock extends Block {
    public String name;
    public String password;
    public boolean createtable = false;
    public boolean droptable = false;
    public boolean createdatabase = false;
    public boolean dropdatabase = false;
    public boolean grant = false;
    public boolean normal = false;

    public UserBlock(String name, String password) {
        this.name = name;
        this.password = password;
        if (name.equals("system")) {
            createdatabase = true;
            dropdatabase = true;
            createtable = true;
            droptable = true;
            grant = true;
            normal = true;
        }
    }
}
