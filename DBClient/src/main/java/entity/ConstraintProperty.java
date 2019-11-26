package entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConstraintProperty {
    private StringProperty constraintName;
    private StringProperty constraintType;
    private StringProperty constraintParam;
    private StringProperty fieldName;

    public ConstraintProperty(String constraintName, String constraintType, String constraintParam,String fieldName) {
        this.constraintName = new SimpleStringProperty(constraintName);
        this.constraintType = new SimpleStringProperty(constraintType);
        this.constraintParam = new SimpleStringProperty(constraintParam);
        this.fieldName = new SimpleStringProperty(fieldName);
    }

    public String getConstraintName() {
        return constraintName.get();
    }

    public StringProperty constraintNameProperty() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName.set(constraintName);
    }

    public String getConstraintType() {
        return constraintType.get();
    }

    public StringProperty constraintTypeProperty() {
        return constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType.set(constraintType);
    }

    public String getConstraintParam() {
        return constraintParam.get();
    }

    public StringProperty constraintParamProperty() {
        return constraintParam;
    }

    public String getFieldName() {
        return fieldName.get();
    }

    public StringProperty fieldNameProperty() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName.set(fieldName);
    }

    public void setConstraintParam(String constraintParam) {
        this.constraintParam.set(constraintParam);
    }
}
