package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(get tables)(.+?)(ENDOFSQL)")
public class GetTables extends Parser {
    public GetTables(String originSQL) {
        super(originSQL);
    }

    public String getDatabaseName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}
