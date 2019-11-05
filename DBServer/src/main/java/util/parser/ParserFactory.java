package util.parser;

import util.parser.parsers.DeleteParser;
import util.parser.parsers.InsertParser;
import util.parser.parsers.Parser;
import util.parser.parsers.UpdateParser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.SQL.*;

public class ParserFactory {
    public static Parser generateParser(String sql) {
        sql = sql.trim();
        sql = sql.toLowerCase();
        sql = sql.replaceAll("\\s+", " ");
        sql = sql.substring(0, sql.lastIndexOf(";"));
        sql += (" " + END_OF_SQL);

        if (contains(sql, "(insert into)(.+)(values)(.+)")) return new InsertParser(sql);
        else if (contains(sql, "(update)(.+)(set)(.+)")) return new UpdateParser(sql);
        else if (contains(sql, "(delete from)(.+)")) return new DeleteParser(sql);
        else if (contains(sql, "(create database)(.+)")) {
            //TODO: fix create database
        }
        else if (contains(sql, "(create table)(.+)")) {
            //TODO: fix create table
        }
        else if (contains(sql, ("(drop table)(.+)"))) {
            //TODO: fix drop table
        }
        else if (contains(sql, "(drop database)(.+)")) {
            //TODO: fix drop database
        }
        //TODO: alter table {table name} (add column|modify column|drop column)
        return null;
    }

    private static boolean contains(String sql, String reg) {
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }
}
