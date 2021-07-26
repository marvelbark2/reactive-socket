package ws.prospeak.project.socket.reactivex.server.myown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketListener {
    private final int port;
    private final Logger logger = LoggerFactory.getLogger(SocketListener.class);
    private final Observer<SocketEvent> socketEventObserver = new Observer<>();

    public SocketListener(int port) {
        this.port = port;
    }

    public void subscribe(Listener<SocketEvent> listener) {
        socketEventObserver.subscribe(listener);
    }

    public void start() {
        logger.info("Server Socket started");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService executorService = Executors.newCachedThreadPool();
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> socketEventObserver.publish(new SocketEvent(socket)));
            }
        } catch (IOException e) {
           logger.error(e.getMessage(), e);
        }
    }
}
