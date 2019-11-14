package util.parser.parsers;

import util.parser.annoation.Body;
import util.parser.annoation.End;
import util.parser.annoation.Permission;
import util.parser.annoation.Start;

import java.util.List;

@Permission(Permission.CREATE_TABLE)
@Start("(insert into)(.+?)([(]|values)")
@Body("([(])(.+?)([)] values|[)]values)")
@End("(values[(]|values [(])(.+)([)])")
public class InsertParser extends Parser {
    public InsertParser(String originSQL) {
        super(originSQL);
    }

    @Override
    public String getParsesSQL() {
        return super.getParsesSQL() + ")";
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public List<String> getInsertField() {
        return splitOriginSQLIntoSegment().get(1);
    }

    public List<String> getInsertValue() {
        return splitOriginSQLIntoSegment().get(2);
    }
}
