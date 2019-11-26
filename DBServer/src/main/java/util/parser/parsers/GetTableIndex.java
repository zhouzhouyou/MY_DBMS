package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(get table_index)(.+?)(ENDOFSQL)")
public class GetTableIndex extends Parser {
    public GetTableIndex(String originSQL) {
        super(originSQL);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}
