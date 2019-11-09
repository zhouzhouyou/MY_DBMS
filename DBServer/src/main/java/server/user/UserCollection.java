package server.user;

import util.file.BlockCollection;

public class UserCollection extends BlockCollection<UserBlock> {
    static final String absolutePath = "./system.user";

    public UserCollection() {
        super(absolutePath);
    }
}
