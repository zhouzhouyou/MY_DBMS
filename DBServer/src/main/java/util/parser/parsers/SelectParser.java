package util.parser.parsers;


import util.parser.annoation.Body;
import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.CREATE_TABLE)
@Start("(select)(.+)(from)")
@Body("(from)(.+)(where|on|ENDOFSQL)")
public class SelectParser extends Parser {
    public SelectParser(String originSQL) {
        super(originSQL);
    }
}
