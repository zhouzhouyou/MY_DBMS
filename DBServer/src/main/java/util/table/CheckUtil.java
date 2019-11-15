package util.table;

import util.file.RandomAccessFiles;
import util.result.Result;

import java.util.Map;

public class CheckUtil {

    /**
     * 判断是否该条记录是否满足特定条件
     *
     * @param recordMap 一条记录
     * @param raf 随机访问工具
     * @param param 条件，可能是多条
     * @return 是否满足check约束
     * @see javax.script.ScriptEngineManager
     */
    public static Result check(Map<String, Object> recordMap, RandomAccessFiles raf, String param) {
        //TODO: 首先，根据and,or将param拆开，然后得到true,false，并变成"true and (false or true)"类似的结构
        // 最后使用ScriptEngineManager判断最终是true还是false

        return null;
    }
}
