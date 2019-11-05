package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(drop table)(.+?)(ENDOFSQL)")
public class DropTableParser extends Parser{
    public DropTableParser(String originSQL) {
        super(originSQL);
    }
}
