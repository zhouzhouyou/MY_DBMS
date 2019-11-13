package util.parser.parsers;

import util.SQL;
import util.parser.annoation.Body;
import util.parser.annoation.End;
import util.parser.annoation.Permission;
import util.parser.annoation.Start;

import static util.SQL.DESC;

@Permission(Permission.NORMAL)
@Start("(index)(.+?)(on)")
@Body("(on)(.+?)([(])")
@End("([(])(.+?)([)])")
public class CreateIndexParser extends Parser {
    public CreateIndexParser(String originSQL) {
        super(originSQL);
    }

    public String getIndexName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(1).get(0);
    }

    private String getField() {
        return splitOriginSQLIntoSegment().get(2).get(0);
    }

    public String getFieldName() {
        return getField().split(" ")[0];
    }

    public boolean isUnique() {
        return originSQL.contains(SQL.UNIQUE);
    }

    public boolean getFieldAsc() {
        String[] strings = getField().split(" ");
        if (strings.length == 1) return true;
        else return !strings[1].equals(DESC);
    }
}
