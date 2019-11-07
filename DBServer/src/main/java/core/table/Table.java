package core.table;

import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;
import core.file.SimpleFile;

import java.util.Date;

public class Table extends SimpleFile {
    public int recordAmount;
    public int fieldAmount;
    public String definePath;
    public String constraintPath;
    public String recordPath;
    public String indexPath;
    public Date createTime;
    public Date lastChangeTime;

    public Table(String path, String prefix,
                 int recordAmount, int fieldAmount,
                 String definePath, String constraintPath, String recordPath, String indexPath,
                 Date createTime, Date lastChangeTime) throws EmptyNameException, IllegalNameException {
        super(path, prefix, "table");
        this.recordAmount = recordAmount;
        this.fieldAmount = fieldAmount;
        this.definePath = definePath;
        this.constraintPath = constraintPath;
        this.recordPath = recordPath;
        this.indexPath = indexPath;
        this.createTime = createTime;
        this.lastChangeTime = lastChangeTime;
    }
}
