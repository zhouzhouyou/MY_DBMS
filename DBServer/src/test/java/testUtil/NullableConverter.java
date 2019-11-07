package testUtil;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.DefaultArgumentConverter;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import util.parser.annoation.Body;

public final class NullableConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        if (source == null) {
            return null;
        }
        return DefaultArgumentConverter.INSTANCE.convert(source, targetType);
    }
}
