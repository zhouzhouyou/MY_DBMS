package util.parser;

import util.parser.parsers.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.SQL.END_OF_SQL;

public class ParserFactory {

    public static Parser generateParser(String sql) {
        sql = sql.trim();
        sql = sql.toLowerCase();
        sql = sql.replaceAll("\\s+", " ");
        if (sql.contains(";")) sql = sql.substring(0, sql.lastIndexOf(";"));
        sql += (" " + END_OF_SQL);

        if (contains(sql, "(insert into)(.+?)(values)(.+)")) return new InsertParser(sql);
        else if (contains(sql, "(update)(.+)(set)(.+)")) return new UpdateParser(sql);
        else if (contains(sql, "(delete from)(.+)")) return new DeleteParser(sql);
        else if (contains(sql, "(create database)(.+)")) return new CreateDatabaseParser(sql);
        else if (contains(sql, "(create table)(.+)")) return new CreateTableParser(sql);
        else if (contains(sql, "(drop table)(.+)")) return new DropTableParser(sql);
        else if (contains(sql, "(drop database)(.+)")) return new DropDatabaseParser(sql);
        else if (contains(sql, "(disconnect)")) return new DisconnectParser(sql);
        else if (contains(sql, "(connect)(.+)")) return new ConnectParser(sql);
        else if (contains(sql, "(select)(.+)(from)(.+)")) return new SelectParser(sql);
        else if (contains(sql, "(choose database)(.+?)")) return new ChooseDatabaseParser(sql);
        else if (contains(sql, "(release database)")) return new ReleaseDatabaseParser(sql);
        else if (contains(sql, "(create index|create unique index)(.+?)(on)"))
            return new CreateIndexParser(sql);
        else if (contains(sql, "(drop index)(.+)(on)([(])(.+?)([)])")) return new DropIndexParser(sql);
        //TODO: alter table {table name} (add column|modify column|drop column)
        //TODO: select
        return null;
    }

    private static boolean contains(String sql, String reg) {
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }
}
