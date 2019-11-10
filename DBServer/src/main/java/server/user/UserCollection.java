package server.user;

import util.file.BlockCollection;

import static util.file.Path.USER_PATH;

public class UserCollection extends BlockCollection<UserBlock> {
    public UserCollection() {
        super(USER_PATH);
    }
}
