package ClientWindow.controllers;

import ClientWindow.MessageStream;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import util.Request;
import util.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class ClientController extends Tab {


    public TextArea outputArea;
    public TextArea inputArea;
    private BufferedReader input;
    private PrintStream output;
    private String currentDatabase = null;
    private MessageStream stream;


    public ClientController(Socket socket) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClientController.class.getResource("/ClientWindow.xml"));
        loader.setRoot(this);
        loader.setController(this);

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintStream(socket.getOutputStream());
        stream = new MessageStream(outputArea, inputArea);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Result getResult(String sql) {
        Request request = new Request(null, sql);
        String result = "";
        try {
            output.println(request.getGson());
            result = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.formGson(result);
    }

    private Result getResult(String sql, String databaseName) {
        Request request = new Request(databaseName, sql);
        String result = "";
        try {
            output.println(request.getGson());
            result = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.formGson(result);
    }


    public void getInput(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String inputContext = stream.getInput();
            handleMessage(inputContext);
        }
    }

    private void handleMessage(String inputContext) {
        if (inputContext.contains("choose database")) {
            inputContext = inputContext.replace("choose database", "").trim();
            currentDatabase = inputContext;
        } else {
            if (currentDatabase != null) {
                Result result = getResult(inputContext);
                stream.printResult(result,null);
            } else {
                Result result = getResult(inputContext, currentDatabase);
                stream.printResult(result,null);
            }

        }

    }
}
