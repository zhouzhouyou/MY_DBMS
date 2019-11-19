package util.parser.condition;

import util.parser.parsers.Parser;

import static util.parser.ParserUtil.contains;
import static util.parser.ParserUtil.preProcess;

public class ConditionParserFactory {
    public static Parser grenadeConditionParser(String param) {
        param = preProcess(param);
        if (contains(param, "(.+?)(<|>|<>|!=|=|<=|>=)")) return new ComparisionCondition(param);
        else if (contains(param, "(.+?)(\\s+in|\\s+not in)")) return new InCondition(param);
        return null;
    }
}
