package util.parser.parsers;

import util.parser.annoation.Start;
import util.parser.annoation.Body;
import util.parser.annoation.End;

import static util.SQL.AND_OR_SPLIT;

@Start("(update)(.+)(set)")
@Body("(set)(.+?)( where | ENDOFSQL)")
@End(value = "(where)(.+)(ENDOFSQL)", split = AND_OR_SPLIT)
public class UpdateParser extends Parser {
    public UpdateParser(String originSQL) {
        super(originSQL);
    }
}
