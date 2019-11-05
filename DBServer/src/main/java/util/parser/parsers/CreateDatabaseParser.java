package util.parser.parsers;

import util.parser.annoation.Start;

@Start("(create database)(.+?)(ENDOFSQL)")
public class CreateDatabaseParser extends Parser {
    public CreateDatabaseParser(String originSQL) {
        super(originSQL);
    }
}
