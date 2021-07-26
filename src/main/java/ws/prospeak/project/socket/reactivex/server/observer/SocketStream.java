package ws.prospeak.project.socket.reactivex.server.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.concurrent.Flow;

public class SocketStream implements Flow.Subscriber<Socket> {
    private final Logger logger = LoggerFactory.getLogger(SocketStream.class);
    private Flow.Subscription connections = null;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.connections = subscription;
        connections.request(1);
    }

    @Override
    public void onNext(Socket socket) {
        logger.info("Socket on line: {}", socket.getRemoteSocketAddress());
    }

    @Override
    public void onError(Throwable throwable) {
        logger.error(throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        logger.trace("Subscription done");
    }
}
