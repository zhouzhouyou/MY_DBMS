import Client.Client;

import java.io.*;
import java.net.Socket;

public class ClientStarter {
//    public static final int SUCCESS = 200;
//    public static final int BAD_REQUEST = 400;
//    public static final int UNAUTHORIZED = 401;
//    public static final int NOT_FOUND = 404;
//    public static final int CONFLICT = 409;
//    private static final String connect = "connect system 123456";
//    private static final String disconnect = "disconnect";
//    private static final String create_database = "create database ckf";
//    private static final String drop_database = "drop database ckf";

    public static void main(String[] args) throws IOException {
//        String result = "";
//        String sql = "";
//        Scanner scanner = new Scanner(System.in);
//        scanner.useDelimiter("\n");
//        Socket socket = new Socket("localhost", 10086);
//        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        PrintStream output = new PrintStream(socket.getOutputStream());
//        Client.Result resultSet = null;
//        boolean connected = false;
//        Gson gson = new Gson();
//
//        while (true) {
//            sql = scanner.next();
//            sql = sql.trim();
//            if (sql.equals("") || sql.length() == 0) {
//                System.out.println("Check Your Input.");
//            } else if (sql.contains("connect") && !sql.contains("disconnect")) {
//                output.println(sql);
//                result = input.readLine();
//                resultSet = gson.fromJson(result, Client.Result.class);
//                if (resultSet.code == SUCCESS) {
//                    connected = true;
//                    System.out.println("Success to connect.");
//                } else System.out.println("Fail to connect.");
//            } else if (connected) {
//                output.println(sql);
//                result = input.readLine();
//                resultSet = gson.fromJson(result, Client.Result.class);
//                if (sql.contains("disconnect") && resultSet.code == SUCCESS){
//                    connected = false;
//                    System.out.println("Disconnect.");
//                }
//
//                System.out.println(result);
//            } else if (sql.contains("quit")) {
//                break;
//            } else System.out.println("Please connect first.");
//        }
//
//        input.close();
//        output.close();
//        socket.close();
        Socket socket = new Socket("localhost",10086);
        new Thread(new Client(socket)).start();
    }


//    private static class Client.Result {
//        int code;
//        Object data;
//
//        public Client.Result(int code, Object data) {
//            this.code = code;
//            this.data = data;
//        }
//    }
}
