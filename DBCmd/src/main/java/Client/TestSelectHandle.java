package Client;

import util.Result;

import java.util.*;


public class TestSelectHandle {
    public static void main(String[] args) {
        HashMap<String, List> data = new HashMap<String, List>();
        String name = "name";
        String age = "age";

        List<String> names = new ArrayList<String>();
        List<Integer> ages = new ArrayList<Integer>();
        names.add("ckf");
        names.add("zzy");
        names.add("dsy");

        ages.add(18);
        ages.add(18);
        ages.add(18);

        data.put(name, names);
        data.put(age, ages);

        Result result = new Result(200, data);
        handleSelectResult(result);

    }

    private static void handleSelectResult(Result result) {
//        Map resultSet = (Map) util.result.data;
//
//        Object[] dataLengthGetter = resultSet.values().toArray();
//        List tempDataLengthGetter = (List) dataLengthGetter[0];
//        int dataLength = tempDataLengthGetter.size();
//
//        StringBuilder line = new StringBuilder();
//        for (Object field : resultSet.keySet()) {
//            String key = (String) field;
//            line.append(key).append("     ");
//        }
//        System.out.println(line);
//        System.out.println();
//        line.delete(0, line.length());
//
//        for (int i = 0; i < dataLength; i++) {
//            for (Object dataList : resultSet.values()) {
//                List data = (List) dataList;
//                line.append(data.get(i)).append("     ");
//            }
//            System.out.println(line);
//            line.delete(0, line.length());
//        }

        Map resultSet = (Map) result.data;

//        Set<String> fieldNames = resultSet.keySet();
//        StringBuilder line = new StringBuilder();
//        fieldNames.forEach(s -> line.append(s).append("     "));
//        System.out.println(line);
//        System.out.println();
//        line.delete(0, line.length());//new ?
//        List<List<Object>> dataList = new LinkedList<>();
//        fieldNames.forEach(s -> dataList.add(resultSet.get(s)));

        Object[] dataLengthGetter = resultSet.values().toArray();
        List tempDataLengthGetter = (List) dataLengthGetter[0];
        int dataLength = tempDataLengthGetter.size();

        Set fieldNames = resultSet.keySet();
        StringBuilder line = new StringBuilder();
        fieldNames.forEach(s -> line.append(s).append("     "));
//        for (Object field : resultSet.keySet()) {
//            String key = (String) field;
//            line.append(key).append("     ");
//        }
        System.out.println(line);
        System.out.println();
        line.delete(0, line.length());

        List<List<Object>> dataLists = new LinkedList<>();
        fieldNames.forEach(s -> dataLists.add((List<Object>) resultSet.get(s)));
        for (int i = 0; i < dataLength; i++) {
            for (Object dataList : dataLists) {
                List dataItem = (List) dataList;
                line.append(dataItem.get(i)).append("     ");
            }
            System.out.println(line);
            line.delete(0, line.length());
        }

    }
}
