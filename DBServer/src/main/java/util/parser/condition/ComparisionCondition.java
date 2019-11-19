package util.parser.condition;

import util.parser.annoation.Body;
import util.parser.annoation.Start;
import util.parser.parsers.Parser;
import util.result.Result;
import util.table.CheckUtil;
import util.table.ConvertUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Start("(check)(.+?)(<=|<>|!=|>=|=|<|>)")
@Body("(<=|<>|!=|>=|=|<|>)(.+?)(ENDOFSQL)")
public class ComparisionCondition extends Parser {
    private String cmp;

    public ComparisionCondition(String originParam) {
        super("check " + originParam);
    }

    public String getLeftValue() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public String getRightValue() {
        return splitOriginSQLIntoSegment().get(1).get(0);
    }

    public Result check(String leftValue, String rightValue) {
        String reg = "<=|<>|!=|>=|=|<|>";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(originSQL);

        if (matcher.find()) {
            cmp = matcher.group();
        }
        if (cmp.equals("=")) cmp = "==";
        if (ConvertUtil.getType(leftValue) == -1)
            return CheckUtil.grenadeResult(String.format("'%s' %s '%s'", leftValue, cmp, rightValue));
        return CheckUtil.grenadeResult(String.format("%s %s %s", leftValue, cmp, rightValue));
    }
}
