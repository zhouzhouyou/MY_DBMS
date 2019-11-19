package util.parser.condition;

public abstract class WhereItem {
    public String whereCondition;

    public WhereItem(String whereCondition) {
        this.whereCondition = whereCondition;
    }

    public String getResultString() {
        return getResult() ? "true" : "false";
    }

    public abstract boolean getResult();
}
