package util.parser.parsers;


import util.parser.annoation.Body;
import util.parser.annoation.Parts;
import util.parser.annoation.Permission;
import util.parser.annoation.Start;

import java.util.List;

import static util.SQL.AND_OR_SPLIT;
import static util.SQL.DEFAULT_SPLIT;

@Permission(Permission.CREATE_TABLE)
@Parts({"(select)(.+)(from)", DEFAULT_SPLIT,
        "(from)(.+?)(where|group by|having|order by|ENDOFSQL)", DEFAULT_SPLIT,
        "(where|on)(.+?)(group by|having|order by|ENDOFSQL)", DEFAULT_SPLIT,
        "(group by)(.+?)(having|order by|ENDOFSQL)", DEFAULT_SPLIT,
        "(having)(.+?)(order by|ENDOFSQL)", DEFAULT_SPLIT,
        "(order by)(.+)(ENDOFSQL)", DEFAULT_SPLIT})
public class SelectParser extends Parser {
    public SelectParser(String originSQL) {
        super(originSQL);
    }

    public List<String> getSelectItem() {
        return splitOriginSQLIntoSegment().get(0);
    }

    public List<String> getTable() {
        return splitOriginSQLIntoSegment().get(1);
    }

    public List<String> getWhereCondition() {
        return splitOriginSQLIntoSegment().get(2);
    }

    public List<String> getGroupBy() {
        return splitOriginSQLIntoSegment().get(3);
    }

    public List<String> getHaving() {
        return splitOriginSQLIntoSegment().get(4);
    }

    public List<String> getOrder() {
        return splitOriginSQLIntoSegment().get(5);
    }
}
