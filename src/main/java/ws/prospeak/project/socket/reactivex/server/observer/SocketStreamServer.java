package ws.prospeak.project.socket.reactivex.server.observer;

import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.BiConsumer;

public class SocketStreamServer extends SubmissionPublisher<Socket> {
    public SocketStreamServer(int i) {
        super(Executors.newSingleThreadExecutor(), i);
    }

    public SocketStreamServer() {
        super(Executors.newSingleThreadExecutor(), 5);
    }
}
