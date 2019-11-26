package util;

import java.util.HashMap;
import java.util.Map;

public enum Bundle {
    INSTANCE;

    private Map<String, Object> map = new HashMap<>();

    public Object get(String key) {
        return map.get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }
}
