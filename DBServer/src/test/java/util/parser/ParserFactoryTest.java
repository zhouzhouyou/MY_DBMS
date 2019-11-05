package util.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import util.parser.parsers.*;

import java.util.List;

import static util.SQL.*;

class ParserFactoryTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "insert into student (name, sex) values ('zzy', 'male);",
            "delete from student where name='zzy' or sex='male';",
            "update student set sex='male' where name='zzy';",
            "create table student (" +
                    "id integer primary key," +
                    "name varchar not null," +
                    "sex varchar not null," +
                    "birthday datetime default sysdate);",
            "create database yuri;",
            "drop table student;",
            "drop database yuri;",
    })
    public void testSQL(String sql) {
        Parser parser = ParserFactory.generateParser(sql);
        assert parser != null;
        if (parser instanceof InsertParser) System.out.println(INSERT_INTO);
        else if (parser instanceof DeleteParser) System.out.println(DELETE_FROM);
        else if (parser instanceof UpdateParser) System.out.println(UPDATE);
        else if (parser instanceof CreateTableParser) System.out.println(CREATE_TABLE);
        else if (parser instanceof CreateDatabaseParser) System.out.println(CREATE_DATABASE);
        else if (parser instanceof DropTableParser) System.out.println(DROP_TABLE);
        else if (parser instanceof DropDatabaseParser) System.out.println(DROP_DATABASE);
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