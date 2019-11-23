package util.table;

import util.SQL;
import util.parser.condition.ComparisionCondition;
import util.parser.condition.ConditionParserFactory;
import util.parser.condition.InCondition;
import util.parser.parsers.Parser;
import util.result.Result;
import util.result.ResultFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

import static util.SQL.*;

public class CheckUtil {


    /**
     * 判断是否该条记录是否满足特定条件
     *
     * @param recordMap 一条记录
     * @param param     条件，可能是多条
     * @return 是否满足check约束
     * @see javax.script.ScriptEngineManager
     */
    public static Result check(Map<String, Object> recordMap, String param) {
        //TODO: 首先，根据and,or将param拆开，然后得到true,false，并变成"true and (false or true)"类似的结构
        // 最后使用ScriptEngineManager判断最终是true还是false
        StringBuilder sb = new StringBuilder();
        String[] strings = param.split(String.format(SQL.WITH_DELIMITER, SQL.AND_OR_SPLIT));
        for (String string : strings) {
            string = string.trim();
            if (string.equals(" ") || string.equals("")) continue;
            switch (string) {
                case AND:
                    sb.append(AA);
                    break;
                case OR:
                    sb.append(OO);
                    break;
                case LB:
                case RB:
                    sb.append(string);
                    break;
                default:
                    Parser parser = ConditionParserFactory.grenadeConditionParser(string);
                    if (parser == null) return ResultFactory.buildFailResult(string);
                    Result result = checkChild(parser, recordMap);
                    if (result.code == ResultFactory.NOT_FOUND) return result;
                    sb.append(result.code == ResultFactory.SUCCESS);
                    break;
            }
        }
        return grenadeResult(sb.toString());
    }

    /**
     * 返回子条件的真假
     *
     * @param parser    子条件的checkSQL解析
     * @param recordMap 记录
     * @return 子条件的真假
     */
    private static Result checkChild(Parser parser, Map<String, Object> recordMap) {
        if (parser instanceof InCondition) return checkInChild((InCondition) parser, recordMap);
        else return checkCmpChild((ComparisionCondition) parser, recordMap);
    }

    /**
     * 返回对应变量或常量的值
     *
     * @param name      变量名
     * @param recordMap 记录
     * @return 对应变量或常量的值
     */
    private static String getValue(String name, Map<String, Object> recordMap) {
        if (name.startsWith("'")) {
            if (name.endsWith("'")) return ConvertUtil.getString(name);
            return null;
        } else {
            if (name.endsWith("'")) return null;
            Object o = recordMap.get(name);
            return o == null ? name : String.valueOf(o);
        }
    }

    /**
     * 返回比较类型checkSQL解析器
     *
     * @param parser    比较类型checkSQL解析器
     * @param recordMap 记录
     * @return 子条件的真假
     */
    private static Result checkCmpChild(ComparisionCondition parser, Map<String, Object> recordMap) {
        String leftValue = parser.getLeftValue();
        String rightValue = parser.getRightValue();
        String left = getValue(leftValue, recordMap);
        String right = getValue(rightValue, recordMap);

        if (left == null || right == null)
            return ResultFactory.buildObjectNotExistsResult(leftValue + " " + rightValue);
        return parser.check(left, right);
    }

    /**
     * 返回包含类型checkSQL解析器
     *
     * @param parser    包含类型checkSQL解析器
     * @param recordMap 记录
     * @return 子条件的真假
     */
    private static Result checkInChild(InCondition parser, Map<String, Object> recordMap) {
        String str = parser.getValue();
        String value = getValue(str, recordMap);
        if (value == null) return ResultFactory.buildObjectNotExistsResult(str);
        return parser.check(value);
    }

    /**
     * 利用javax的库，获取check最终的真假，
     * 类似"true and (true or false)"(请自动把and和or脑补成对应符号）
     *
     * @param string true和false的组合
     * @return check最终的真假
     */
    public static Result grenadeResult(String string) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            boolean eval = (boolean) engine.eval(string);
            if (eval) return ResultFactory.buildSuccessResult(string);
        } catch (ScriptException e) {
            return ResultFactory.buildFailResult(e.toString());
        }
        return ResultFactory.buildFailResult(string);
    }
}
