package util.parser.parsers;

import util.parser.annoation.Body;
import util.parser.annoation.Start;

@Start("(drop index)(.+?)(on)")
@Body("(on)(.+)(ENDOFSQL)")
public class DropIndexParser extends Parser {
    public DropIndexParser(String originSQL) {
        super(originSQL);
    }

    public String getIndexName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(1).get(0);
    }
}
