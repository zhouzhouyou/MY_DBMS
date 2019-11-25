package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(get table_constraint)(.+?)(ENDOFSQL)")
public class GetTableConstraint extends Parser {
    public GetTableConstraint(String originSQL) {
        super(originSQL);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}