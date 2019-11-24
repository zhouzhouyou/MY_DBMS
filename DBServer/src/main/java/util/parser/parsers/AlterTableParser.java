package util.parser.parsers;


import util.SQL;
import util.parser.annoation.Body;
import util.parser.annoation.Start;


@Start("(alter table)(.+?)(add|drop|modify)")
@Body("(column|constraint)(.+?)( ENDOFSQL)")
public class AlterTableParser extends Parser {
    public enum alterType {
        ADD_COLUMN,
        DROP_COLUMN,
        ADD_CONSTRAINT,
        DROP_CONSTRAINT,
        MODIFY_COLUMN,
        MODIFY_CONSTRAINT
    }

    public AlterTableParser(String originSQL) {
        super(originSQL);
    }

    public String getTableName() {
        return splitOriginSQLIntoSegment().get(0).get(0);
    }

    public alterType getAlterType() {
        String[] strings = originSQL.split(" ");
        String type = strings[3] + " " + strings[4];
        switch (type) {
            case SQL.ADD_COLUMN:
                return alterType.ADD_COLUMN;
            case SQL.DROP_COLUMN:
                return alterType.DROP_COLUMN;
            case SQL.MODIFY_COLUMN:
                return alterType.MODIFY_COLUMN;
            case SQL.ADD_CONSTRAINT:
                return alterType.ADD_CONSTRAINT;
            case SQL.DROP_CONSTRAINT:
                return alterType.DROP_CONSTRAINT;
            case SQL.MODIFY_CONSTRAINT:
                return alterType.MODIFY_CONSTRAINT;
            default:
                return null;
        }

        //Map<String, alterType> map = new HashMap<>();
//        if (strings[3].equals(ADD)) {
//            if (strings[4].equals(COLUMN))
//                return alterType.ADD_COLUMN;
//            else if (strings[4].equals(CONSTRAINT))
//                return alterType.ADD_CONSTRAINT;
//        } else if (strings[3].equals(DROP)) {
//            if (strings[4].equals(COLUMN))
//                return alterType.DROP_COLUMN;
//            else if (strings[4].equals(CONSTRAINT))
//                return alterType.DROP_CONSTRAINT;
//        } else if (strings[3].equals(MODIFY)) {
//            if (strings[4].equals(COLUMN))
//                return alterType.MODIFY_COLUMN;
//            else if (strings[4].equals(CONSTRAINT))
//                return alterType.MODIFY_CONSTRAINT;
//        }
    }

    public String getOperationContent() {
        String[] strings = originSQL.split(" ");
        return originSQL.substring(originSQL.indexOf(strings[5]));
    }

}
