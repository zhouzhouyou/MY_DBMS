package entity;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.SQL;


public class DefineProperty {
    public final SimpleStringProperty fieldName;
    public final SimpleStringProperty fieldType;
    public final SimpleStringProperty pk;
    public final SimpleStringProperty notNull;
    public final SimpleStringProperty unique;
    public final SimpleStringProperty check;
    public final SimpleStringProperty defaultValue;

    public static final int PK = 0;
    public static final int FK = 1;
    public static final int CHECK = 2;
    public static final int UNIQUE = 3;
    public static final int NOT_NULL = 4;
    public static final int DEFAULT = 5;
    public static final int IDENTITY = 6;

    public DefineProperty(String fieldName, String fieldType, String pk, String notNull, String unique, String check, String defaultValue) {
        this.fieldName = new SimpleStringProperty(fieldName);
        this.fieldType = new SimpleStringProperty(fieldType);
        this.pk = new SimpleStringProperty(pk);
        this.notNull = new SimpleStringProperty(notNull);
        this.unique = new SimpleStringProperty(unique);
        this.check = new SimpleStringProperty(check);
        this.defaultValue = new SimpleStringProperty(defaultValue);
    }
}
