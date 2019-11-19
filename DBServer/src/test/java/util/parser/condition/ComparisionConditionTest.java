package util.parser.condition;

import org.junit.jupiter.api.Test;

class ComparisionConditionTest {
    @Test
    public void test() {
        ComparisionCondition comparisionCondition = new ComparisionCondition("a<=b");
        comparisionCondition.splitOriginSQLIntoSegment().forEach(list -> list.forEach(System.out::println));
        System.out.println(comparisionCondition.check("'a'", "'b'").code);
    }

}