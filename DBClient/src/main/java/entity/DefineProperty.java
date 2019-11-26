package entity;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.SQL;


public class DefineProperty {
    private final SimpleStringProperty fieldName;
    private final SimpleStringProperty fieldType;
    private final SimpleStringProperty pk;
    private final SimpleStringProperty notNull;
    private final SimpleStringProperty unique;
    private final SimpleStringProperty check;
    private final SimpleStringProperty defaultValue;

    public DefineProperty(String fieldName, String fieldType, String pk, String notNull, String unique, String check, String defaultValue) {
        this.fieldName = new SimpleStringProperty(fieldName);
        this.fieldType = new SimpleStringProperty(fieldType);
        this.pk = new SimpleStringProperty(pk);
        this.notNull = new SimpleStringProperty(notNull);
        this.unique = new SimpleStringProperty(unique);
        this.check = new SimpleStringProperty(check);
        this.defaultValue = new SimpleStringProperty(defaultValue);
    }

    public void setFieldName(String fieldName) {
        this.fieldName.set(fieldName);
    }

    public void setFieldType(String fieldType) {
        this.fieldType.set(fieldType);
    }

    public void setPk(String pk) {
        this.pk.set(pk);
    }

    public void setNotNull(String notNull) {
        this.notNull.set(notNull);
    }

    public void setUnique(String unique) {
        this.unique.set(unique);
    }

    public void setCheck(String check) {
        this.check.set(check);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue.set(defaultValue);
    }

    public String getFieldName() {
        return fieldName.get();
    }

    public SimpleStringProperty fieldNameProperty() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType.get();
    }

    public SimpleStringProperty fieldTypeProperty() {
        return fieldType;
    }

    public String getPk() {
        return pk.get();
    }

    public SimpleStringProperty pkProperty() {
        return pk;
    }

    public String getNotNull() {
        return notNull.get();
    }

    public SimpleStringProperty notNullProperty() {
        return notNull;
    }

    public String getUnique() {
        return unique.get();
    }

    public SimpleStringProperty uniqueProperty() {
        return unique;
    }

    public String getCheck() {
        return check.get();
    }

    public SimpleStringProperty checkProperty() {
        return check;
    }

    public String getDefaultValue() {
        return defaultValue.get();
    }

    public SimpleStringProperty defaultValueProperty() {
        return defaultValue;
    }
}
