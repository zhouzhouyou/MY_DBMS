package ClientWindow.controllers;

import ClientWindow.MessageStream;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import util.Request;
import util.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientWindowController implements Initializable {
    public static FXMLLoader loader = new FXMLLoader(ClientWindowController.class.getResource("/ClientWindow.fxml"));
    public TextArea outputArea;
    public TextArea inputArea;
    private BufferedReader input;
    private PrintStream output;
    private MessageStream stream;
    private String currentDatabase = null;

    public void initialize(URL location, ResourceBundle resources) {
    }

    private Result getResult(String sql) {
        Request request = new Request(currentDatabase, sql);
        String result = "";
        try {
            output.println(request.getGson());
            result = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.formGson(result);
    }

    public void sendRequest(MouseEvent mouseEvent) {
        String data = inputArea.getText().trim();
        if (data.contains("choose database")) {
            data = data.replace("choose database", "").trim();
            currentDatabase = data;
            outputArea.appendText("Operation Success" + "\n");
        } else if (data.contains("connect")) {
            currentDatabase = null;
            Result result = getResult(data);
            stream.printResult(result, data);
        } else {
            Result result = getResult(data);
            stream.printResult(result, data);
        }
    }

    public void connectToServer(MouseEvent mouseEvent) throws IOException {
        Socket socket = new Socket("localhost", 10086);
        stream = new MessageStream(outputArea, inputArea);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintStream(socket.getOutputStream());

        outputArea.appendText("connect to server..." + "\n");
    }
}
