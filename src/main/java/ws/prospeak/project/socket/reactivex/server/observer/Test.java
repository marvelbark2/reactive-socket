package ws.prospeak.project.socket.reactivex.server.observer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Flow;

public class Test {
    public static void main(String[] args) {
       Server server = new Server(61022);
       server.subscribe(Test::log);
       server.start();
    }

    static void log(Socket socket) {
        try {
          PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
          writer.println("Hello there");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
