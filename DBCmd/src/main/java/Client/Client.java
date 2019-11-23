package Client;

import util.Request;
import util.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintStream output;
    private boolean connected = false;
    private String currentDatabase = null;
    private int operationID = 0;
    private Scanner scanner = new Scanner(System.in);

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintStream(socket.getOutputStream());
        scanner.useDelimiter("\n");
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

    private void printOperationHint() {
        System.out.println("You want to : ");
        if (!connected) {
            System.out.println("1. connect to data center.");
            System.out.println("2. exit.");
        } else {
            System.out.println("3. create or drop database");
            System.out.println("4. do database operation.");
            System.out.println("5. disconnect");
        }
        operationID = scanner.nextInt();
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
        System.out.println(line);
        System.out.println();
        line.delete(0, line.length());

        for (int i = 0; i < dataLength; i++) {
            for (Object dataList : resultSet.values()) {
                List data = (List) dataList;
                line.append(data.get(i)).append("     ");
            }
            System.out.println(line);
            line.delete(0, line.length());
        }
    }


    public void run() {
        while (true) {
            printOperationHint();
            if (operationID == 1) {
                System.out.println("Please input connect statement.");
                String connectSql = scanner.next();
                Result result = getResult(connectSql);
                if (result.code == Result.SUCCESS)
                    connected = true;
                else {
                    System.out.println(result.data.toString());
                }
            } else if (operationID == 2) {
                break;
            } else if (operationID == 3) {
                System.out.println("Please input create/drop database statement.");
                String sql = scanner.next();
                Result result = getResult(sql);
                if (result.code != Result.SUCCESS)
                    System.out.println(result.data.toString());
            } else if (operationID == 4) {
                if (currentDatabase != null) {
                    System.out.println("Do you want to change database? (y/n)");
                    if (scanner.next().equals("y")) {
                        System.out.println("Please input database name.");
                        currentDatabase = scanner.next();
                    }
                } else {
                    System.out.println("Please input database name.");
                    currentDatabase = scanner.next();
                }
                System.out.println("Please input sql statement.");
                String sql = scanner.next();
                Result result = getResult(sql, currentDatabase);
                if (result.code == Result.SUCCESS && sql.contains("select")) {
                    handleSelectResult(result);
                } else
                    System.out.println(result.data.toString());
            } else if (operationID == 5) {
                Result result = getResult("disconnect");
                if (result.code == Result.SUCCESS) {
                    connected = false;
                    System.out.println("disconnect success");
                }

            } else {
                System.out.println("Please check the number you input");
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

}
