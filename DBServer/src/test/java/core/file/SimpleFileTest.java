package core.file;

import com.google.gson.Gson;
import core.file.exception.EmptyNameException;
import core.file.exception.IllegalNameException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class SimpleFileTest {
    private static final String path = "./";

    static Stream<Arguments> stringProvider() {
        return Stream.of(
                Arguments.of(path, "yuri1", "db"),
                Arguments.of(path, "yuri2", "tic"),
                Arguments.of(path, "yuri3", ""),
                Arguments.of(path, "", "trd"),
                Arguments.of(path, "*", "db"),
                Arguments.of(path, "$", "db"),
                Arguments.of(path, "\\", "tic")
        );
    }

    @ParameterizedTest
    @MethodSource("stringProvider")
    public void testGSON(String path, String prefix, String postfix) {
        try {
            SimpleFile simpleFile = new SimpleFile(path, prefix, postfix);
            Gson gson = new Gson();
            System.out.println(gson.toJson(simpleFile));
        } catch (EmptyNameException | IllegalNameException e) {
            System.out.println(e);
        }
    }
}