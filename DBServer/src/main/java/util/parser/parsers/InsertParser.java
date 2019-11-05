package util.parser.parsers;

import util.parser.annoation.Start;
import util.parser.annoation.Body;
import util.parser.annoation.End;

@Start("(insert into)(.+?)([(])")
@Body("([(])(.+)([)] values )")
@End("([)] values [(])(.+)([)])")
public class InsertParser extends Parser {
    public InsertParser(String originSQL) {
        super(originSQL);
    }

    @Override
    public String getParsesSQL() {
        return super.getParsesSQL() + ")";
    }
}
