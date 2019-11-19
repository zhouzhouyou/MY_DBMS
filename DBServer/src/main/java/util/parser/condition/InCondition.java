package util.parser.condition;

import util.parser.annoation.Body;
import util.parser.annoation.Start;
import util.parser.parsers.Parser;
import util.result.Result;
import util.result.ResultFactory;

@Start("(check)(.+?)(in|not int)")
@Body("([(])(.+)([)])")
public class InCondition extends Parser {
    public InCondition(String originSQL) {
        super("check " + originSQL);
    }

    public String getValue() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public Result check(String value) {
        boolean result = splitOriginSQLIntoSegment().get(1).contains(value);
        if (originSQL.split(" ")[1].trim().equals("not")) result = !result;
        if (result) return ResultFactory.buildSuccessResult(value);
        return ResultFactory.buildFailResult(value);
    }
}
