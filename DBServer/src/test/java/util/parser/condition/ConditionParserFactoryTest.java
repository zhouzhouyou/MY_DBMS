package util.parser.condition;

import org.junit.jupiter.api.Test;
import util.parser.parsers.Parser;

class ConditionParserFactoryTest {

    @Test
    void grenadeConditionParser() {
        //System.out.println(Objects.requireNonNull(ConditionParserFactory.grenadeConditionParser("")).getClass());
        Parser parser = ConditionParserFactory.grenadeConditionParser("'a' in (5, 6)");
        assert parser instanceof InCondition;
        System.out.println(((InCondition) parser).check("5").code);
        ;
    }
}