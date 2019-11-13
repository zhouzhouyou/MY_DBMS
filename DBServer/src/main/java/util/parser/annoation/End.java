package util.parser.annoation;

import java.lang.annotation.*;

import static util.SQL.DEFAULT_SPLIT;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface End {
    String value();

    String split() default DEFAULT_SPLIT;
}
