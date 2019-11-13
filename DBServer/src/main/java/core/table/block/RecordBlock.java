package core.table.block;

import java.util.Map;

/**
 * 存储着一条记录，暂时无用
 */
public class RecordBlock {
    private Map<String, Object> map;

    public RecordBlock(Map<String, Object> map) {
        this.map = map;
    }

    public Object get(String fieldName) {
        return map.get(fieldName);
    }

}
