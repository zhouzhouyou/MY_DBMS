package core.database;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import util.result.Result;
import util.result.ResultFactory;

import java.io.IOException;

class DatabaseBlockFactoryTest {
    private DatabaseFactory factory = DatabaseFactory.INSTANCE;


    @ParameterizedTest
    @ValueSource(strings = {"yuri", "yuri", ".//."})
    void createDatabase(String name)  {
        Result result = factory.createDatabase(name, false);
        if (result.code == ResultFactory.BAD_REQUEST) System.out.println(result.data);
        else {
            assert factory.exists(name);
            factory.saveInstance();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"yuri", "no"})
    void releaseDatabase(String name) {
        try {
            DatabaseBlock databaseBlock = factory.getDatabase(name);
            databaseBlock.release();
            System.out.println("released");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"yuri", "wtf"})
    void dropDatabase(String name) {
        factory.createDatabase(name, DatabaseFactory.USER);
        try {
            DatabaseBlock block = factory.getDatabase(name);
            try {
                Result result = factory.dropDatabase(name);
                System.out.println(result.code + " " + result.data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                block.release();
                Result result = factory.dropDatabase(name);
                System.out.println(result.code + " " + result.data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}