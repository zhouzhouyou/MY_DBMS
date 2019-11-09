package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(connect)(.+)(ENDOFSQL)")
public class ConnectParser extends Parser {
    public ConnectParser(String originSQL) {
        super(originSQL);
    }

    public String getName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public String getPassword() {
        return splitOriginSQLIntoSegment().get(0).get(1);
    }
}
