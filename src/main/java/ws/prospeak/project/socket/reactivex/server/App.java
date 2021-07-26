package ws.prospeak.project.socket.reactivex.server;


import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.prospeak.project.socket.reactivex.server.rx.ReactiveServer;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

public class App {
    static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) {
        // my pub sub
      /*  SocketListener sl = new SocketListener(63211);
        sl.subscribe(s -> {
            Socket socket = s.getSocket();
            new Exchange(socket);
        });
        sl.start();*/

        DataSource ds;

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
        server.start();
    }
}
