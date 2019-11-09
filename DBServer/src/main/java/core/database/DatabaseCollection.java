package core.database;

import util.file.BlockCollection;


class DatabaseCollection extends BlockCollection<DatabaseBlock> {
    static final String absolutePath = "./system.db";


    public DatabaseCollection() {
        super(absolutePath);
    }
}
