package util.parser.parsers;

import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.GRANT)
@Start("(drop user)(.+?)(ENDOFSQL)")
public class DropUserParser extends Parser {
    public DropUserParser(String originSQL) {
        super(originSQL);
    }

    public String getUserName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}
