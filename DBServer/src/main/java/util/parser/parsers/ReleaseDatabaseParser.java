package util.parser.parsers;

import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.NORMAL)
@Start("(release database)")
public class ReleaseDatabaseParser extends Parser {
    public ReleaseDatabaseParser(String originSQL) {
        super(originSQL);
    }
}
