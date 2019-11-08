package core.table;

import core.database.DatabaseBlock;
import core.database.DatabaseFactory;
import core.table.block.TableBlock;
import core.table.factory.TableFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Stream;

class TableFactoryTest {
    private DatabaseFactory databaseFactory = DatabaseFactory.INSTANCE;
    private DatabaseBlock databaseBlock = databaseFactory.getDatabase("yuri");
    private TableFactory factory = databaseBlock.getFactory();

    TableFactoryTest() throws Exception {
    }

    static Stream<Arguments> createProvider() {
        return Stream.of(
                Arguments.of("yuri", 5, 5, "definePath", "constraintPath", "recordPath", "indexPath", new Date(), new Date()),
                Arguments.of("ckf", 5, 5, "definePath", "constraintPath", "recordPath", "indexPath", new Date(), new Date()),
                Arguments.of(".//.", 5, 5, "definePath", "constraintPath", "recordPath", "indexPath", new Date(), new Date())
        );
    }

    @ParameterizedTest
    @MethodSource("createProvider")
    void createTable(String tableName, int recordAmount, int fieldAmount, String definePath,
                     String constraintPath,
                     String recordPath, String indexPath, Date createTime, Date lastChangeTime) {
        Result result = factory.createTable(tableName, recordAmount, fieldAmount, definePath,
                constraintPath, recordPath, indexPath, createTime, lastChangeTime);
        Result result1 = factory.createTable(tableName, recordAmount, fieldAmount, definePath,
                constraintPath, recordPath, indexPath, createTime, lastChangeTime);
        if (result.code == ResultFactory.BAD_REQUEST)
            System.out.println(result.data);
        else {
            assert factory.exists(tableName);
            factory.saveInstance();
        }
        if (result1.code == ResultFactory.BAD_REQUEST)
            System.out.println(result1.data);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ckf","yuri"})
    void releaseTable(String tableName){
        try {
            TableBlock tableBlock = factory.getTable(tableName);
            tableBlock.release();
            System.out.println("released");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @MethodSource("createProvider")
    void dropTable(String tableName, int recordAmount, int fieldAmount, String definePath,
                   String constraintPath,
                   String recordPath, String indexPath, Date createTime, Date lastChangeTime) {
        factory.createTable(tableName, recordAmount, fieldAmount, definePath,
                constraintPath, recordPath, indexPath, createTime, lastChangeTime);
        try {

            TableBlock block = factory.getTable(tableName);
            try {
                Result result = factory.dropTable(tableName);
                System.out.println(result.code + " " + result.data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                block.release();
                Result result = factory.dropTable(tableName);
                System.out.println(result.code + " " + result.data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}