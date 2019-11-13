package util.parser.parsers;

import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.DROP_TABLE)
@Start("(drop table)(.+?)(ENDOFSQL)")
public class DropTableParser extends Parser {
    public DropTableParser(String originSQL) {
        super(originSQL);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}
