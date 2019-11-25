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

    public static List<Pair<String, Object>> buildPairList(List<String> strings, List<Object> objects) {
        List<Pair<String, Object>> list = new ArrayList<>();
        int min = Math.min(strings.size(), objects.size());
        for (int i = 0; i < min; i++) {
            Pair<String, Object> pair = new Pair<>(strings.get(i), objects.get(i));
            list.add(pair);
        }
        return list;
    }

    public static Map<String, List<Object>> buildAllPairList(List<String> strings, List<List<Object>> objects) {
        Map<String, List<Object>> map = new HashMap<>();
        List<List<Object>> temp = new ArrayList<>();

        for (int i = 0; i < strings.size(); i++) {
            temp.add(new ArrayList<>());
        }

        for (List<Object> record : objects) {
            for (int i = 0; i < record.size(); i++) {
                temp.get(i).add(record.get(i));
            }
        }

        for (int i = 0; i < strings.size(); i++) {
            map.put(strings.get(i), temp.get(i));
        }

        return map;
    }

    public static Object getObject(List<Pair<String, Object>> record, String fieldName) {
        for (Pair<String, Object> pair : record) {
            if (pair.getFirst().equals(fieldName)) return pair.getLast();
        }
        return null;
    }

    public static List<Object> fromPairList(List<Pair<String, Object>> record) {
        List<Object> objects = new ArrayList<>();
        record.forEach(stringObjectPair -> objects.add(stringObjectPair.getLast()));
        return objects;
    }

    public static Map<String, Object> fromPairListToMap(List<Pair<String, Object>> record) {
        Map<String, Object> map = new HashMap<>();
        record.forEach(stringObjectPair -> map.put(stringObjectPair.getFirst(), stringObjectPair.getLast()));
        return map;
    }

}
