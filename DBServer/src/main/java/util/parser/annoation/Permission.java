package util.parser.annoation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    String value();

    String CREATE_TABLE = "createtable";
    String CREATE_DATABASE = "createdatabase";
    String DROP_TABLE = "droptable";
    String DROP_DATABASE = "dropdatabase";
    String GRANT = "grant";
    String NORMAL = "normal";
}
