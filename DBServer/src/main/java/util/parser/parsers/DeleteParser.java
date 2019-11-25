package util.parser.parsers;

import util.parser.annoation.Body;
import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.CREATE_TABLE)
@Start("(delete from)(.+?)(where|ENDOFSQL)")
@Body("(where)(.+)( ENDOFSQL)")
public class DeleteParser extends Parser {
    public DeleteParser(String originSQL) {
        super(originSQL);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public boolean deleteAll() {
        return splitOriginSQLIntoSegment().size() == 1;
    }

    public String getWhereCondition() {
        return splitOriginSQLIntoSegment().get(1).get(0);
    }
}
