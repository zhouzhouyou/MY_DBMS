package util.parser.parsers;

import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.GRANT)
@Start("(create user)(.+)(ENDOFSQL)")
public class CreateUserParser extends Parser {
    public CreateUserParser(String originSQL) {
        super(originSQL);
    }

    public String[] getStrings() {
        return splitOriginSQLIntoSegment().get(0).get(0).split(" ");
    }

    public boolean isValid() {
        if (splitOriginSQLIntoSegment().get(0).size() == 0) return false;
        return getStrings().length == 2;
    }

    public String getUserName() {
        return getStrings()[0];
    }

    public String getPassword() {
        return getStrings()[1];
    }
}
