package util.parser;

import util.parser.parsers.*;

import static util.parser.ParserUtil.contains;
import static util.parser.ParserUtil.preProcess;

public class ParserFactory {

    public static Parser generateParser(String sql) {
        sql = preProcess(sql);

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
        else if (contains(sql, "(alter table)(.+?)(add|drop column|add(\\s+)constraint|modify)"))
            return new AlterTableParser(sql);
        else if (contains(sql, ("(create user)(.+)"))) return new CreateUserParser(sql);
        else if (contains(sql, "(drop user)(.+)")) return new DropUserParser(sql);
        else if (contains(sql, "(get databases)")) return new GetDatabases(sql);
        else if (contains(sql, ("(grant|revoke)(.+)"))) return new GrantParser(sql);
        else if (contains(sql, "(get tables)(.+)")) return new GetTables(sql);
        else if (contains(sql, ("(get table_define)(.+)"))) return new GetTableDefine(sql);
        //TODO: alter table {table name} (add column|modify column|drop column)
        //TODO: select
        return null;
    }
}
