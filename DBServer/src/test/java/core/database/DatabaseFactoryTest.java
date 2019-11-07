package core.database;

import core.file.exception.IllegalNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import util.result.DefaultResult;
import util.result.Result;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseFactoryTest {
    private DatabaseFactory factory = DatabaseFactory.INSTANCE;


    @ParameterizedTest
    @ValueSource(strings = {"yuri", "yuri", ".//."})
    void createDatabase(String name)  {
        if (factory.exists(name)) {
            System.out.println(name + " exists");
            return;
        }
        Result result = factory.createDatabase(name, false);
        if (result instanceof DefaultResult) {
            System.out.println(result.data);
            return;
        }
        assertTrue(factory.exists(name));
        Database database = null;
        try {
            database = factory.getDatabase(name);
        } catch (IOException | ClassNotFoundException | IllegalNameException e) {
            e.printStackTrace();
            return;
        }
        assertEquals(database.filename.split(".")[0], name);
        System.out.println(database.filename);
    }

    @Test
    void exists() {
    }

    @ParameterizedTest
    @ValueSource(strings = {"yuri"})
    void releaseDatabase(String name) {
        try {
            Database database = factory.getDatabase(name);
            database.release();
            System.out.println("released");
        } catch (IOException | ClassNotFoundException | IllegalNameException e) {
            System.out.println(e);
        }
    }

    @Test
    void getDatabase() {
    }

    @ParameterizedTest
    @ValueSource(strings = {"yuri", "wtf"})
    void dropDatabase(String name) {
        try {
            Result result = factory.dropDatabase(name);
            System.out.println(result.code + " " + result.data);
        } catch (IOException | ClassNotFoundException | IllegalNameException e) {
            System.out.println(e);
        }
    }
}