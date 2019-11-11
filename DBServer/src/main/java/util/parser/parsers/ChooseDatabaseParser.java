package util.parser.parsers;

import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.CREATE_DATABASE)
@Start("(choose database)(.+?)(ENDOFSQL)")
public class ChooseDatabaseParser extends Parser {
    public ChooseDatabaseParser(String originSQL) {
        super(originSQL);
    }

    public String getDatabaseName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}
