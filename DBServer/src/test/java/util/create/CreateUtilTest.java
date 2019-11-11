package util.create;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


class CreateUtilTest {
    @ParameterizedTest
    @ValueSource(strings = "varchar(20)")
    public void testSplit(String string) {
        assert CreateUtil.getParameter(string) == 20;
    }

    @ParameterizedTest
    @ValueSource(strings = "dsada check(to get) dsad")
    public void testCheck(String string) {
        System.out.println(CreateUtil.getCheck(string));
    }

    @ParameterizedTest
    @ValueSource(strings = "dsad default 20")
    public void testDefault(String string) {
        System.out.println(CreateUtil.getDefault(string));
    }
}