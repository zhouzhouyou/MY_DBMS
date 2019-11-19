package util.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ParserUtilTest {
    static Stream<Arguments> provider() {
        return Stream.of(
                Arguments.of("a not in (a, b)", "(.+?)(in|not in)"),
                Arguments.of("a in (a, b)", "(.+?)(in|not in)"),
                Arguments.of("a < 5", "(.+?)(<|>|<>|!=|=|<=|>=)"),
                Arguments.of("a<=5", "(.+?)(<|>|<>|!=|=|<=|>=)"),
                Arguments.of("a not <> 5", "(.+?)(<|>|<>|!=|=|<=|>=)")
        );
    }

    @ParameterizedTest
    @MethodSource("provider")
    void contains(String sql, String reg) {
        System.out.println(ParserUtil.contains(sql, reg));
    }
}