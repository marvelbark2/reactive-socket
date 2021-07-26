package ws.prospeak.project.socket.reactivex.server.rx;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReactiveServer {
    private final Logger logger = LoggerFactory.getLogger(ReactiveServer.class);

    private final int port;
    private CompositeDisposable socketDisposable;
    public final Subject<ReactiveSocket> connections = ReplaySubject.create();
    public ReactiveServer(int port) {
        this.port = port;
        this.socketDisposable = new CompositeDisposable();
    }


    public void start() {
        logger.info("Starting reactive server");
        try {
            ServerSocket server = new ServerSocket(port);
            ExecutorService service = Executors.newCachedThreadPool();
            this.connections.doOnError(err -> logger.error(err.getMessage(), err)).subscribe(
                    ReactiveSocket::close
            );
            while (true) {
                Socket socket = server.accept();
                logger.info("New client here");
                Observable
                        .just(socket)
                        .repeat()
                        .map(x -> new ReactiveSocket(x))
                        .subscribe(x -> {
                            connections.onNext(x);

                        });
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
