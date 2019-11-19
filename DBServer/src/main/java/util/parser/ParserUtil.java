package util.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.SQL.END_OF_SQL;

public class ParserUtil {
    public static String preProcess(String sql) {
        sql = sql.trim();
        sql = sql.toLowerCase();
        sql = sql.replaceAll("\\s+", " ");
        if (sql.contains(";")) sql = sql.substring(0, sql.lastIndexOf(";"));
        sql += (" " + END_OF_SQL);
        return sql;
    }

    public static boolean contains(String sql, String reg) {
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }
}
