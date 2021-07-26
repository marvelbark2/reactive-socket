package ws.prospeak.project.socket.reactivex.server;


import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.prospeak.project.socket.reactivex.server.myown.SocketListener;
import ws.prospeak.project.socket.reactivex.server.rx.ReactiveServer;

import javax.sql.DataSource;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ForkJoinPool;

public class App {
    static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws InterruptedException {
        // simple pub sub
      /*  */
        var pool = ForkJoinPool.commonPool();

        SocketListener sl = new SocketListener(63211);
        sl.subscribe(s -> {
            Socket socket = s.getSocket();
            logger.info("Socket {}", socket.getRemoteSocketAddress());
        });

        // rxJava
        ReactiveServer server = new ReactiveServer(63210);
        server.connections.subscribe(reactiveSocket -> {
            logger.info("Connections has been subscribed");
            String received = reactiveSocket.received();
            System.out.println(received);
            Observable<String> messages = Observable.just("Hello world");
            messages.subscribe(message -> {
                byte[] msg = message.getBytes(StandardCharsets.UTF_8);
                reactiveSocket.sendAsync(msg);
            });
        }, System.out::println, () -> logger.info("Subsc is done"));


        pool.submit(sl::start);
        pool.submit(server::start);

        Thread.currentThread().join();
    }
}
