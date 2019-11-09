package util.parser.parsers;

import util.parser.annoation.Permission;
import util.parser.annoation.Start;

@Permission(Permission.CREATE_DATABASE)
@Start("(create database)(.+?)(ENDOFSQL)")
public class CreateDatabaseParser extends Parser {
    public CreateDatabaseParser(String originSQL) {
        super(originSQL);
    }

    public String getDatabaseName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }
}
