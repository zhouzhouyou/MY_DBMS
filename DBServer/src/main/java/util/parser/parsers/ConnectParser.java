package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(connect)(.+)(ENDOFSQL)")
public class ConnectParser extends Parser {
    public ConnectParser(String originSQL) {
        super(originSQL);
    }

    private String[] getInfo() {
        return splitOriginSQLIntoSegment().get(0).get(0).split("\\s+");
    }

    public String getName() {
        return getInfo()[0];
    }

    public String getPassword() {
        return getInfo()[1];
    }
}
