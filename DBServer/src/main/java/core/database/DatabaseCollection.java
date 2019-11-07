package core.database;

import core.file.BlockCollection;
import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;


class DatabaseCollection extends BlockCollection<DatabaseBlock> {
    static final String absolutePath = "./system.db";


    public DatabaseCollection() {
        super(absolutePath);
    }
}
