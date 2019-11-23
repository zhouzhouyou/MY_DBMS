package ClientWindow;

import javafx.scene.control.TextArea;
import util.Result;

import java.util.List;
import java.util.Map;

import java.util.concurrent.locks.ReentrantLock;

public class MessageStream {
    private TextArea outputArea;
    private TextArea inputArea;
    private ReentrantLock lock = new ReentrantLock();

    public MessageStream(TextArea outputArea, TextArea inputArea) {
        this.outputArea = outputArea;
        this.inputArea = inputArea;
    }

    public synchronized String getInput() {
        return inputArea.getText().trim();
    }

    public synchronized void printResult(Result result) {
        if (result.data instanceof Map)
            handleSelectResult(result);
        else if (result.code != Result.SUCCESS)
            outputArea.appendText(result.data.toString() + "\n");

    }

    private void handleSelectResult(Result result) {
        Map resultSet = (Map) result.data;

        Object[] dataLengthGetter = resultSet.values().toArray();
        List tempDataLengthGetter = (List) dataLengthGetter[0];
        int dataLength = tempDataLengthGetter.size();

        StringBuilder line = new StringBuilder();
        for (Object field : resultSet.keySet()) {
            String key = (String) field;
            line.append(key).append("     ");
        }
//        System.out.println(line);
//        System.out.println();
        outputArea.appendText(line + "/n/n");
        line.delete(0, line.length());

        for (int i = 0; i < dataLength; i++) {
            for (Object dataList : resultSet.values()) {
                List data = (List) dataList;
                line.append(data.get(i)).append("     ");
            }
//            System.out.println(line);
            outputArea.appendText(line + "/n/n");
            line.delete(0, line.length());
        }
    }
}
