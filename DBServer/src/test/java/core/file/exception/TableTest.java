package core.file.exception;

import com.google.gson.Gson;
import core.table.Table;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.stream.Stream;

class TableTest {
    static Stream<Arguments> provider() {
        return Stream.of(
                Arguments.of("student", 0, 0, "define", "constraint", "record", "index", new Date(), new Date())
        );
    }

    @ParameterizedTest
    @MethodSource("provider")
    public void testGSON(String path, String prefix,
                         int recordAmount, int fieldAmount,
                         String definePath, String constraintPath, String recordPath, String indexPath,
                         Date createTime, Date lastChangeTime) {
        try {
            Table table = new Table(path, prefix,
                    recordAmount, fieldAmount,
                    definePath, constraintPath, recordPath, indexPath,
                    createTime, lastChangeTime);
            Gson gson = new Gson();
            System.out.println(gson.toJson(table));
        } catch (EmptyNameException | IllegalNameException e) {
            System.out.println(e);
        }
    }

}