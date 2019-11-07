package core.database;

import core.file.SimpleFile;
import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;

import java.util.Date;

public class Database extends SimpleFile {

    public static final String DB_PATH = "./";
    public static final String DB_POST_FIX = "db";

    public static final boolean SYSTEM = true;
    public static final boolean USER = false;

    public boolean type = USER;
    public Date createTime;

    public Database(String prefix, boolean type, Date createTime) throws EmptyNameException, IllegalNameException {
        super(DB_PATH, prefix, DB_POST_FIX);
        this.type = type;
        this.createTime = createTime;
    }

    /**
     * Release the database resource from factory so it can be dropped.
     */
    public void release() {
        DatabaseFactory.INSTANCE.releaseDatabase(this);
    }
}
