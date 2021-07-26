package ws.prospeak.project.socket.reactivex.server.rx;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class ReactiveSocket {
    private final Logger logger = LoggerFactory.getLogger(ReactiveSocket.class);
    private final int receiveBufferSize = 8192;
    private final boolean disposed;
    public int maximumBufferSize = 1024 * 1024;
    private Socket socket;
    private Disposable readSubscription;
    private BlockingCollection<Byte> received;
    private Observable<Byte> receiver;
    private Subject<Byte> sender;

    public ReactiveSocket(Socket socket) {
        this.disposed = false;
        connect(socket);
    }

    private void connect(Socket clientSocket) {
        if (clientSocket == null) {
            throw new NullPointerException("Socket is null");
        }
        if (disposed) {
            throw new RuntimeException(this.getClass().getName());
        }
        if (clientSocket.isClosed() || !clientSocket.isConnected()) {
            throw new RuntimeException("Socket is closed in server side");
        }
        if (this.socket == clientSocket) {
            logger.info("Switching to an other socket");
            disconnect();
        }
        if (clientSocket.equals(socket) && isConnected(clientSocket)) {
            logger.info("This socket is already connected");
            return;
        }
        this.setSocketReceiveBufferSize(clientSocket);
        this.socket = clientSocket;
        this.received = new BlockingCollection<>();
        this.receiver = this.received.observe();
        this.sender = BehaviorSubject.create();
        if (readSubscription != null) {
            readSubscription.dispose();
        }
        logger.info("Initialized !");
        beginRead();
    }

    private void setSocketReceiveBufferSize(Socket socket) {
        try {
            socket.setReceiveBufferSize(receiveBufferSize);
        } catch (SocketException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private boolean isConnected() {
        return isConnected(socket);
    }

    private boolean isConnected(Socket socket) {
        if (socket == null) {
            logger.warn("Socket is nullll");
            return false;
        } else {
            return socket.isConnected();
        }
    }

    private void disconnect() {
        if (!isConnected()) {
            throw new RuntimeException("Error the socket is already disconnected");
        }
        disconnect(false);
    }

    private void disconnect(boolean disposing) {
        if (disposed && !disposing) {
            throw new RuntimeException("Error! Threading issue");
        }
        if (readSubscription != null) {
            readSubscription.dispose();
        }
        readSubscription = null;
        if (isConnected(socket)) {
            close(socket);
        }
    }

    public void close(Socket socket) {
        if (socket == null) {
            throw new NullPointerException("Socket is null");
        }
        if (!isConnected(socket)) {
            throw new NullPointerException("Socket is already disconnected");
        }
        try {
            socket.close();
        } catch (IOException ioException) {
            logger.error(ioException.getLocalizedMessage(), ioException);
        }
    }

    private void beginRead() {
        logger.info("Begin to read");
        try {
            InputStream stream = socket.getInputStream();
            this.readSubscription = Observable
                    .defer(() -> {
                        byte[] transBuffer = new byte[this.receiveBufferSize];
                        Runnable read = () -> {
                            try {
                                var d = stream.read(transBuffer);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        };
                        read.run();
                        Byte[] buffer = toByte(transBuffer);
                        return Observable.fromArray(buffer);
                    })
                    .repeat()
                    .takeWhile(x -> x != 0)
                    .subscribe(
                            x -> {
                                this.received.add(x);
                                logger.info("Receiving {}", x);
                            },
                            err -> {
                                logger.error("Unknown error " + err.getMessage(), err);
                                disconnect(false);
                            },
                            () -> disconnect(false)
                    );
        } catch (IOException ioException) {
            logger.error(ioException.getLocalizedMessage(), ioException);
        }
    }

    public Object sendAsync(byte[] bytes) {
        if (disposed) {
            throw new RuntimeException("CLOSED");
        }
        if (!isConnected(socket)) {
            throw new RuntimeException("S CLOSED");
        }
        CountDownLatch latch = new CountDownLatch(1);

        return Observable
                .defer(() -> {
                    try {
                        latch.await();
                        OutputStream stream = socket.getOutputStream();
                        stream.write(bytes);
                    } finally {
                        latch.countDown();
                    }
                    return Observable.fromArray(bytes);
                })
                .repeat()
                .takeWhile(Objects::nonNull)
                .subscribe(
                        x -> logger.info("Bytes: {}", bytes)
                );
    }


    public String received() {
        if (disposed) {
            throw new RuntimeException("Error");
        }
        List<Byte> bytes = new LinkedList<>();
        received.observe().subscribe(
                x -> bytes.add(x)
        );
        return new String(toLocalByte(bytes.toArray(new Byte[0])));
    }


    private byte[] toLocalByte(Byte[] bytes) {
        byte[] bits = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bits[i] = bytes[i];
        }
        return bits;
    }

    private Byte[] toByte(byte[] bytes) {
        Byte[] bits = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bits[i] = bytes[i];
        }
        return bits;
    }

    public void close () {
        close(socket);
    }
}
