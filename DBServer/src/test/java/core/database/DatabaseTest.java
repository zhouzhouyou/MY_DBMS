package core.database;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    static Stream<Arguments> provider() {
        return Stream.of(
                Arguments.of("yuri", Database.SYSTEM, new Date()),
                Arguments.of("yuri", Database.USER, new Date()),
                Arguments.of("yuri", false, new Date()),
                Arguments.of("yuri", Database.SYSTEM, null)
        );
    }
}