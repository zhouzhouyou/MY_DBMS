package util.table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import util.result.Result;
import util.result.ResultFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtil {
    public static Date getDate(String string) {
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd");
        sd1.setLenient(true);
        try {
            return sd1.parse(string);
        } catch (ParseException e) {
            //ignore
        }
        SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sd2.setLenient(true);
        try {
            return sd2.parse(string);
        } catch (ParseException e) {
            //ignore
        }
        return null;
    }

    public static Boolean getBoolean(String string) {
        if (string.equals("1") || string.equals("true")) return true;
        if (string.equals("0") || string.equals("false")) return false;
        return null;
    }

    public static Integer getInteger(String string) {
        if (!NumberUtils.isDigits(string)) return null;
        return Integer.parseInt(string);
    }

    public static Double getDouble(String string) {
        if (!StringUtils.isNumeric(string)) return null;
        return Double.valueOf(string);
    }

    public static String getString(String string) {
        if (!string.startsWith("'") || !string.endsWith("'")) return null;
        return string.substring(1, string.length() - 1);
    }

    public static Result getConvertedObject(String string, int type) {
        Object convert = null;
        switch (type) {
            case FieldTypes.INTEGER:
                convert = getInteger(string);
                break;
            case FieldTypes.BOOL:
                convert = getBoolean(string);
                break;
            case FieldTypes.DOUBLE:
                convert = getDouble(string);
                break;
            case FieldTypes.VARCHAR:
                convert = getString(string);
                break;
            case FieldTypes.DATETIME:
                convert = getDate(string);
                break;
        }
        if (convert == null) return ResultFactory.buildInvalidValueConvertResult(FieldTypes.getFieldType(type), string);
        return ResultFactory.buildSuccessResult(convert);
    }

    public static int getType(String value) {
        Object object = null;
        object = getDate(value);
        if (object != null) return FieldTypes.DATETIME;
        object = getBoolean(value);
        if (object != null) return FieldTypes.BOOL;
        object = getInteger(value);
        if (object != null) return FieldTypes.INTEGER;
        object = getDouble(value);
        if (object != null) return FieldTypes.DOUBLE;
        object = getString(value);
        if (object != null) return FieldTypes.VARCHAR;
        return -1;
    }
}
