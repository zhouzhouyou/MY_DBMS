package util.parser.parsers;

import util.parser.annoation.Body;
import util.parser.annoation.Start;



@Start("(create table)(.+?)([(])")
@Body("([(])(.+)([)])")
public class CreateTableParser extends Parser{
    public CreateTableParser(String originSQL) {
        super(originSQL);
    }
}
