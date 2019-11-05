package util;

public class SQL {
    /*Common*/
    public static final String DATABASE = "database";
    public static final String TABLE = "table";

    /*Database*/
    public static final String CREATE_DATABASE = "create database";
    public static final String DROP_DATABASE = "drop database";

    /*Table*/
    public static final String CREATE_TABLE = "create table";
    public static final String DROP_TABLE = "drop table";
    public static final String ALTER = "alter";
    public static final String ADD_COLUMN = "add column";
    public static final String MODIFY_COLUMN = "modify column";
    public static final String DROP_COLUMN = "drop column";

    /*CRUD*/
    public static final String SELECT = "select";
    public static final String ALL = "*";
    public static final String FROM = "from";
    public static final String WHERE = "where";
    public static final String NOT = "not";
    public static final String EXISTS = "exists";
    public static final String IN = "in";
    public static final String INSERT_INTO = "insert into";
    public static final String VALUES = "values";
    public static final String UPDATE = "update";
    public static final String SET = "set";
    public static final String EQUAL = "=";
    public static final String DELETE_FROM = "delete from";

    /*Column Type*/
    public static final String INTEGER = "integer";
    public static final String BOOL = "bool";
    public static final String DOUBLE = "double";
    public static final String VARCHAR = "varchar";
    public static final String DATETIME = "datetime";

    /*Constraint*/
    public static final String PRIMARY_KEY = "primary key";
    public static final String FOREIGN_KEY = "foreign key";
    public static final String CHECK = "check";
    public static final String UNIQUE = "unique";
    public static final String NOT_NULL = "not null";
    public static final String DEFAULT = "default";
    public static final String IDENTITY = "identity";

    /*Security*/
    public static final String CONNECT = "connect";
    public static final String DISCONNECT = "disconnect";
    public static final String GRANT = "grant";

    /*Other*/
    public static final String END_OF_SQL = "ENDOFSQL";
    public static final String LB = "(";
    public static final String RB = ")";
    public static final String DEFAULT_SPLIT = "[,]";
    public static final String AND_OR_SPLIT = "(and|or)";
}
