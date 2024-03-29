package entity;

import java.io.Serializable;

public class Constraint implements Serializable {
    public String constraintName;
    public String fieldName;
    public String constraintType;
    public Object param;

    public Constraint(String constraintName, String fieldName, String constraintType, Object param) {
        this.constraintName = constraintName;
        this.fieldName = fieldName;
        this.constraintType = constraintType;
        this.param = param;
    }

    public ConstraintProperty constraintProperty() {
        return new ConstraintProperty(constraintName, constraintType, param == null ? "" : param.toString(), fieldName);
    }
}
