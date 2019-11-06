package util.parser.parsers;

import util.parser.annoation.Body;
import util.parser.annoation.Start;

@Start("(delete from)(.+?)(where|ENDOFSQL)")
@Body("(where)(.+)( ENDOFSQL)")
public class DeleteParser extends Parser {
    public DeleteParser(String originSQL) {
        super(originSQL);
    }
}
