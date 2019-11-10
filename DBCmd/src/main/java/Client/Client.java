package Client;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;


public class Client implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintStream output;
    private Gson gson;
    private String result = "";
    private String sql = "";
    private boolean connected = false;
    private Result resultSet = null;
    private Scanner scanner = new Scanner(System.in);

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintStream(socket.getOutputStream());
        gson = new Gson();
        scanner.useDelimiter("\n");
    }

    private Result getResultSet(String result) {

        try {
            output.println(sql);
            result = input.readLine();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(result, Result.class);
    }


    public void run() {
        while (true) {
            sql = scanner.next();
            sql = sql.trim();
            if (sql.equals("") || sql.length() == 0) {
                System.out.println("Check Your Input.");
            } else if (sql.contains("connect") && !sql.contains("disconnect")) {
                resultSet = getResultSet(result);
                if (resultSet.code == Result.SUCCESS) {
                    connected = true;
                    System.out.println("Success to connect.");
                } else System.out.println("Fail to connect.");
            } else if (connected) {
                resultSet = getResultSet(result);
                if (sql.contains("disconnect") && resultSet.code == Result.SUCCESS) {
                    connected = false;
                    System.out.println("Disconnect.");
                }
            } else if (sql.contains("quit")) {
                break;
            } else System.out.println("Please connect first.");
        }

        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
