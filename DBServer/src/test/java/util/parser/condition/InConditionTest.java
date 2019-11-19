package util.parser.condition;

import org.junit.jupiter.api.Test;

class InConditionTest {
    @Test
    public void test() {
        InCondition inCondition = new InCondition("20 not in (20, 50, 60)");
        inCondition.splitOriginSQLIntoSegment().forEach(list -> list.forEach(System.out::println));
        System.out.println(inCondition.check("20").code);
    }
}