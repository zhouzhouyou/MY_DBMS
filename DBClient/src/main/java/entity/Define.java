package entity;

import java.io.Serializable;

public class Define implements Serializable {
    public String fieldName;
    public String fieldType;
    public boolean pk;
    public boolean notNull;
    public boolean unique;
    public String check;
    public Object defaultValue;



    public Define(String fieldName, String fieldType, boolean pk, boolean notNull, boolean unique, String check, Object defaultValue) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.pk = pk;
        this.notNull = notNull;
        this.unique = unique;
        this.check = check;
        this.defaultValue = defaultValue;
    }

    public DefineProperty defineProperty() {
        return new DefineProperty(fieldName, fieldType, String.valueOf(pk), String.valueOf(notNull), String.valueOf(unique), check, defaultValue == null? "" : defaultValue.toString());
    }
}
