package util.parser.annoation;

import java.lang.annotation.*;

import static util.SQL.DEFAULT_SPLIT;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Start {
    String value();
    String split() default DEFAULT_SPLIT;
}
