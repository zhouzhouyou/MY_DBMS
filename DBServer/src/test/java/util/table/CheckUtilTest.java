package util.table;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import util.SQL;
import util.result.Result;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class CheckUtilTest {

    @Test
    void check() {
        Map<String, Object> map = new HashMap<>();
        map.put("a", 5);
        map.put("b", 6);
        map.put("c", "how,and,or do you do?");
        Result result = CheckUtil.check(map, "a > 4");
        Result result1 = CheckUtil.check(map, "a > b");
        Result result2 = CheckUtil.check(map, "c = 'how,and,or do you do?'");
        Result result3 = CheckUtil.check(map, "a > 4 or a > b");
        Result result4 = CheckUtil.check(map, "a > 4 and (a > b or c = 'how,and,or do you do?')");
        System.out.println(result.code);
        System.out.println(result1.code);
        System.out.println(result2.code);
        System.out.println(result3.code);
        System.out.println(result4.code);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a > 4 and (b != 'and,or' or c = 6)"
    })
    void checkParser(String sql) {
        Map<String, Object> map = new HashMap<>();
        map.put("a", 5);
        map.put("b", "and,or");
        map.put("c", 6);
        String[] strings = sql.split(String.format(SQL.WITH_DELIMITER, SQL.AND_OR_SPLIT));
        Arrays.asList(strings).forEach(System.out::println);
    }
}