package core.table.block;

import core.table.collection.TableConstraintCollection;

import java.util.Map;

public class RecordBlock {
    private Map<String, Object> map;

    public RecordBlock(Map<String, Object> map) {
        this.map = map;
    }

    public Object get(String fieldName) {
        return map.get(fieldName);
    }

}
