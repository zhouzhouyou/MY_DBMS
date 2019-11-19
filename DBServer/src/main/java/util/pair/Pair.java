package util.pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pair<T, V> {
    private T t;
    private V v;

    public Pair(T t, V v) {
        this.t = t;
        this.v = v;
    }

    public static <T, V> Map<T, V> buildMap(List<T> tList, List<V> vList) {
        Map<T, V> map = new HashMap<>();
        List<Pair<T, V>> list = new ArrayList<>();
        int size = Math.min(tList.size(), vList.size());
        for (int i = 0; i < size; i++) {
            map.put(tList.get(i), vList.get(i));
        }
        return map;
    }

    public static <T, V> List<T> fromMap(Map<V, T> map) {
        return new ArrayList<>(map.values());
    }

    public T getFirst() {
        return t;
    }

    public void setFirst(T t) {
        this.t = t;
    }

    public V getLast() {
        return v;
    }

    public void setLast(V v) {
        this.v = v;
    }

}
