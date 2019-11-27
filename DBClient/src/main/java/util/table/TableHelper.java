package util.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TableHelper {
    public static List<String> getColumns(Map<String, List<Object>> recordMap) {
        return new ArrayList<>(recordMap.keySet());
    }

    public static  List<List<String>> getColumnCells(Map<String, List<Object>> recordMap) {
        List<List<String>> lists = new ArrayList<>();
        int totalSize = 0;
        Iterator<String> it = recordMap.keySet().iterator();
        if (it.hasNext()) {
            totalSize = recordMap.get(it.next()).size();
        }
        for (int i = 0; i < totalSize; i++) {
            List<String> list = new ArrayList<>();
            it = recordMap.keySet().iterator();
            while(it.hasNext()){
                List<Object> tempList = recordMap.get(it.next());
                list.add(tempList.get(i) == null ? null : tempList.get(i).toString());
            }
            lists.add(list);
        }
        return lists;
    }
}
