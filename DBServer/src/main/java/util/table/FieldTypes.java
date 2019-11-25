package util.table;

import util.SQL;

public class FieldTypes {
    public static final int INTEGER = 0;
    public static final int BOOL = 1;
    public static final int DOUBLE = 2;
    public static final int VARCHAR = 3;
    public static final int DATETIME = 4;

    public static final int PK = 0;
    public static final int FK = 1;
    public static final int CHECK = 2;
    public static final int UNIQUE = 3;
    public static final int NOT_NULL = 4;
    public static final int DEFAULT = 5;
    public static final int IDENTITY = 6;


    public static String getFieldType(int type) {
        switch (type) {
            case INTEGER:
                return SQL.INTEGER;
            case BOOL:
                return SQL.BOOL;
            case DOUBLE:
                return SQL.DOUBLE;
            case VARCHAR:
                return SQL.VARCHAR;
            case DATETIME:
                return SQL.DATETIME;
        }
        return null;
    }
}
