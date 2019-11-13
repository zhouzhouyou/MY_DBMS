package util.parser.annoation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String value();

    String CREATE_TABLE = "createTable";
    String CREATE_DATABASE = "createDatabase";
    String DROP_TABLE = "dropTable";
    String DROP_DATABASE = "dropDatabase";
    String GRANT = "grant";
    String NORMAL = "normal";
}
