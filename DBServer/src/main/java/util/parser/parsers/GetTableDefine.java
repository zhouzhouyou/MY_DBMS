package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(get table_define)(.+?)(ENDOFSQL)")
public class GetTableDefine extends Parser {
    public GetTableDefine(String originSQL) {
        super(originSQL);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}
