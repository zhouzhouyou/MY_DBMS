package util.parser.parsers;

import util.SQL;
import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.GRANT)
@Start("(grant|revoke)(.+?)(ENDOFSQL)")
public class GrantParser extends Parser {
    private boolean isGrant;

    public GrantParser(String originSQL) {
        super(originSQL);
        isGrant = originSQL.substring(0, originSQL.indexOf(" ")).equals(SQL.GRANT);
    }

    public String[] getStrings() {
        return splitOriginSQLIntoSegment().get(0).get(0).split(" ");
    }

    public String getUserName() {
        return getStrings()[0];
    }

    public String getGrant() {
        return getStrings()[1];
    }

    public boolean isGrant() {
        return isGrant;
    }
}
