package util.parser.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static util.SQL.DEFAULT_SPLIT;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Body {
    String value();
    String split() default DEFAULT_SPLIT;
}
