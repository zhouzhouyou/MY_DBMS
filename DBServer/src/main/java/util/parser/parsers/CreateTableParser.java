package util.parser.parsers;

import util.parser.annoation.Body;
import util.parser.annoation.Permission;
import util.parser.annoation.Start;

import java.util.List;

@Permission(Permission.CREATE_TABLE)
@Start("(create table)(.+?)([(])")
@Body("([(])(.+)([)])")
public class CreateTableParser extends Parser{
    public CreateTableParser(String originSQL) {
        super(originSQL);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public List<String> getTableDefine() {
        return splitOriginSQLIntoSegment().get(1);
    }
}
