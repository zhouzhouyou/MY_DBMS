package core.table.block;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintBlockTest {

    void check(Map<String, Object> map) {
        map.putIfAbsent("a", "a");
    }

    @Test
    void test() {
        Map<String, Object> map = new HashMap<>();
        map.put("a", null);
        check(map);
        System.out.println(map.get("a"));
    }
}