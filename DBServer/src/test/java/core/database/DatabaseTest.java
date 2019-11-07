package core.database;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Date;
import java.util.stream.Stream;

class DatabaseTest {
    static Stream<Arguments> provider() {
        return Stream.of(
                Arguments.of("yuri", DatabaseFactory.SYSTEM, new Date()),
                Arguments.of("yuri", DatabaseFactory.USER, new Date()),
                Arguments.of("yuri", false, new Date()),
                Arguments.of("yuri", DatabaseFactory.SYSTEM, null)
        );
    }
}