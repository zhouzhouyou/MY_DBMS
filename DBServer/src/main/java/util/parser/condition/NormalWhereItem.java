package util.parser.condition;

public class NormalWhereItem extends WhereItem {
    public NormalWhereItem(String whereCondition) {
        super(whereCondition);
    }


    @Override
    public boolean getResult() {
        return false;
    }
}
