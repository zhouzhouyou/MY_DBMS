package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            ServerSocket serverSocket = new ServerSocket(10086);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                service.submit(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
