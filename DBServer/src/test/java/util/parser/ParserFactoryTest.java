package util.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import util.parser.parsers.DeleteParser;
import util.parser.parsers.InsertParser;
import util.parser.parsers.Parser;
import util.parser.parsers.UpdateParser;

import java.util.List;

import static util.SQL.*;

class ParserFactoryTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "insert into student (name, sex) values ('zzy', 'male);",
            "delete from student where name='zzy' or sex='male';",
            "update student set sex='male' where name='zzy';"
    })
    public void testSQL(String sql) {
        Parser parser = ParserFactory.generateParser(sql);
        assert parser != null;
        if (parser instanceof InsertParser) System.out.println(INSERT_INTO);
        else if (parser instanceof DeleteParser) System.out.println(DELETE_FROM);
        else if (parser instanceof UpdateParser) System.out.println(UPDATE);
        List<List<String>> lists = parser.splitOriginSQLIntoSegment();
        assert lists != null;
        printLists(lists);
    }

    private void printLists(List<List<String>> lists) {
        lists.forEach(strings -> {
            System.out.println("**begin**");
            strings.forEach(System.out::println);
            System.out.println("**end**");
        });
    }
}