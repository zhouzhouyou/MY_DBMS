package util.parser.parsers;

import util.parser.annoation.Body;
import util.parser.annoation.End;
import util.parser.annoation.Start;

import static util.SQL.AND_OR_SPLIT;

@Start("(delete from)(.+)( where|ENDOFSQL)")
@Body(value = "(where)(.+)( ENDOFSQL)", split = AND_OR_SPLIT)
public class DeleteParser extends Parser {
    public DeleteParser(String originSQL) {
        super(originSQL);
    }
}
