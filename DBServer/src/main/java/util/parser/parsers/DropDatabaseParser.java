package util.parser.parsers;

import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.DROP_DATABASE)
@Start("(drop database)(.+?)(ENDOFSQL)")
public class DropDatabaseParser extends Parser {
    public DropDatabaseParser(String originSQL) {
        super(originSQL);
    }

    public String getDatabaseName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}
