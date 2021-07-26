package ws.prospeak.project.socket.reactivex.server.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.prospeak.project.socket.reactivex.server.rx.ReactiveSocketException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {
    private final SocketStreamServer serverStream;
    private final SocketStream socketStream;
    private final int port;
    private final Logger logger = LoggerFactory.getLogger(Server.class);
    public Server(int port) {
        this.serverStream = new SocketStreamServer();
        this.socketStream = new SocketStream();
        this.port = port;
    }

    public void subscribe(Consumer<? super Socket> cb) {
        serverStream.subscribe(socketStream);
        serverStream.consume(cb);
    }

    public void start () {
        logger.trace("Server started");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                serverStream.offer(socket, ((subscriber, socket1) -> {
                    subscriber.onNext(socket1);
                    subscriber.onError(new ReactiveSocketException("Error! Socket subscription"));
                    return true;
                }));
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if(serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
