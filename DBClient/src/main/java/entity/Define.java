package entity;

import java.util.List;

public class Define {
    public String fieldName;
    public String fieldType;
    public List<String> constraints;

    public Define(String fieldName, String fieldType, List<String> constraints) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.constraints = constraints;
    }
}
