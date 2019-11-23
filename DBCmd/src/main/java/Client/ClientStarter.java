package Client;


import java.io.*;
import java.net.Socket;

public class ClientStarter {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 10086);
        new Thread(new Client(socket)).start();
    }
}
