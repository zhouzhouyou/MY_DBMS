package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(drop database)(.+?)(ENDOFSQL)")
public class DropDatabaseParser extends Parser {
    public DropDatabaseParser(String originSQL) {
        super(originSQL);
    }
}
