package ws.prospeak.project.socket.reactivex.server.myown;

public interface Listener<EventType> {
    void ev(EventType e);
}
