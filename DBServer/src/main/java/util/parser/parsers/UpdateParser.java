package util.parser.parsers;

import util.parser.annoation.Body;
import util.parser.annoation.End;
import util.parser.annoation.Permission;
import util.parser.annoation.Start;

import java.util.List;

@Permission(Permission.CREATE_TABLE)
@Start("(update)(.+)(set)")
@Body("(set)(.+?)( where | ENDOFSQL)")
@End("(where)(.+)(ENDOFSQL)")
public class UpdateParser extends Parser {
    public UpdateParser(String originSQL) {
        super(originSQL);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public List<String> getUpdateInfo() {
        return splitOriginSQLIntoSegment().get(1);
    }

    public List<String> getWhereCondition() {
        return splitOriginSQLIntoSegment().get(2);
    }
}
