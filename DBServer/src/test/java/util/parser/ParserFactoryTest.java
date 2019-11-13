package util.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import util.parser.parsers.*;

import java.util.ArrayList;
import java.util.List;

import static util.SQL.*;

class ParserFactoryTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "insert into student (name, sex) values ('zzy', 'male);",
            "insert into student values ('zzy', 'male')",
            "delete from student where name='zzy' or sex='male';",
            "update student set sex='male', birthday=sysdate where name='zzy' or (name='ckf' and age=20);",
            "create table student (" +
                    "id integer primary key," +
                    "name varchar not null," +
                    "sex varchar not null," +
                    "birthday datetime default sysdate);",
            "create database yuri;",
            "drop table student;",
            "drop database yuri;",
            "connect yuri 123456;",
            "choose database yuri",
            "release database",
            "create index a_index on student (sno, age)",
            "create unique index a_index on student (sno desc)",
            "create unique index a_index on student (sno, age desc)"
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


    @ParameterizedTest
    @ValueSource(strings = {
            "select * from student;",
            "select age, name from student;",
            "select * from student where age > 18;",
            "select max(age) from student;",
            "select name, max(age) from student group by name;",
            "select name, grade from student, sc where student.sno = sc.sno;",
            "select name, grade from student, sc where student.sno = sc.sno and grade>60;",
            "select name from student order by age;",
            "select sno, avg(grade) from sc where grade>60 group by sno having avg(grade)>80;"
    })
    public void testSelectSQL(String sql) {
        Parser parser = ParserFactory.generateParser(sql);
        assert parser != null;
        List<List<String>> lists = parser.splitOriginSQLIntoSegment();
        for (int i = 0; i < lists.size(); i++) {
            String type;
            switch (i) {
                case 0:
                    type = "select item";
                    break;
                case 1:
                    type = "table";
                    break;
                case 2:
                    type = "where | on";
                    break;
                case 3:
                    type = "group by";
                    break;
                case 4:
                    type = "having";
                    break;
                default:
                    type = "order by";
                    break;
            }
            System.out.println(type);
            for (String str : lists.get(i)) System.out.println(str);
            System.out.println();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "select avg(grade), cno, cname from sc, course " +
                    "where sc.cno=course.cno and course.credit=3" +
                    "group by cno, cname having avg(grade)>60" +
                    "order by cno, cname"})
    public void testComplexSelectSQL(String sql) {
        Parser parser = ParserFactory.generateParser(sql);
        assert parser instanceof SelectParser;
        List<String> selectItem = new ArrayList<>();
        selectItem.add("avg(grade)");
        selectItem.add("cno");
        selectItem.add("cname");
        assert ((SelectParser) parser).getSelectItem().equals(selectItem);
        List<String> table = new ArrayList<>();
        table.add("sc");
        table.add("course");
        assert ((SelectParser) parser).getTable().equals(table);
        List<String> whereCondition = new ArrayList<>();
        whereCondition.add("sc.cno=course.cno and course.credit=3");
        assert ((SelectParser) parser).getWhereCondition().equals(whereCondition);
        List<String> groupBy = new ArrayList<>();
        groupBy.add("cno");
        groupBy.add("cname");
        assert ((SelectParser) parser).getGroupBy().equals(groupBy);
        List<String> having = new ArrayList<>();
        having.add("avg(grade)>60");
        assert ((SelectParser) parser).getHaving().equals(having);
        List<String> orderBy = new ArrayList<>();
        orderBy.add("cno");
        orderBy.add("cname");
        assert ((SelectParser) parser).getOrder().equals(orderBy);
    }

    private void printLists(List<List<String>> lists) {
        lists.forEach(strings -> {
            System.out.println("**begin**");
            strings.forEach(System.out::println);
            System.out.println("**end**");
        });
    }
}