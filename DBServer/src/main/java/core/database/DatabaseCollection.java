package core.database;

import util.file.BlockCollection;

import static util.file.Path.DATABASE_PATH;

/**
 *
 */
class DatabaseCollection extends BlockCollection<DatabaseBlock> {
    public DatabaseCollection() {
        super(DATABASE_PATH);
    }
}
