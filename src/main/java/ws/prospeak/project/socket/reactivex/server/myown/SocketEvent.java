package ws.prospeak.project.socket.reactivex.server.myown;

import java.net.Socket;

public class SocketEvent {
    private final Socket socket;

    public SocketEvent(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
