package util.parser.parsers;

import util.parser.annoation.Permission;

@Permission(Permission.NORMAL)
public class ReleaseDatabaseParser extends Parser {
    public ReleaseDatabaseParser(String originSQL) {
        super(originSQL);
    }
}
