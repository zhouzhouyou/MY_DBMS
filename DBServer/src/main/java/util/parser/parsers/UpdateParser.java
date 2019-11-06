package util.parser.parsers;

import util.parser.annoation.Start;
import util.parser.annoation.Body;
import util.parser.annoation.End;



@Start("(update)(.+)(set)")
@Body("(set)(.+?)( where | ENDOFSQL)")
@End("(where)(.+)(ENDOFSQL)")
public class UpdateParser extends Parser {
    public UpdateParser(String originSQL) {
        super(originSQL);
    }
}
