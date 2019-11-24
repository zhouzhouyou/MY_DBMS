package ClientWindow;

import javafx.scene.control.TextArea;
import util.Result;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Set;


public class MessageStream {
    private TextArea outputArea;
    private TextArea inputArea;

    public MessageStream(TextArea outputArea, TextArea inputArea) {
        this.outputArea = outputArea;
        this.inputArea = inputArea;
    }

    public synchronized String getInput() {
        return inputArea.getText().trim();
    }

    public synchronized void printResult(Result result, String sql) {
        if (result.code != Result.SUCCESS && result.data != null) {
            outputArea.appendText(result.data.toString() + "\n");
        } else {
            if (sql.contains("select"))
                handleSelectResult(result);
            else
                outputArea.appendText("Operation Success: " + sql + "\n");
        }
    }

    private void handleSelectResult(Result result) {
        Map resultSet = (Map) result.data;

        Object[] dataLengthGetter = resultSet.values().toArray();
        List tempDataLengthGetter = (List) dataLengthGetter[0];
        int dataLength = tempDataLengthGetter.size();

        Set fieldNames = resultSet.keySet();
        StringBuilder line = new StringBuilder();
        fieldNames.forEach(s -> line.append(s).append("     "));
//        System.out.println(line);
//        System.out.println();
        outputArea.appendText(line + "\n\n");
        line.delete(0, line.length());

        List<List<Object>> dataLists = new LinkedList<>();
        fieldNames.forEach(s -> dataLists.add((List<Object>) resultSet.get(s)));
        for (int i = 0; i < dataLength; i++) {
            for (Object dataList : dataLists) {
                List dataItem = (List) dataList;
                line.append(dataItem.get(i)).append("     ");
            }
            outputArea.appendText(line + "\n\n");
            line.delete(0, line.length());
        }

//        for (int i = 0; i < dataLength; i++) {
//            for (Object dataList : resultSet.values()) {
//                List data = (List) dataList;
//                line.append(data.get(i)).append("     ");
//            }
////            System.out.println(line);
//            outputArea.appendText(line + "/n/n");
//            line.delete(0, line.length());
//        }
    }
}
