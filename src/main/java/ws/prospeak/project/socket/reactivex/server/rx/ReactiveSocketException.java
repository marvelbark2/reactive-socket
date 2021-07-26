package ws.prospeak.project.socket.reactivex.server.rx;

public class ReactiveSocketException extends Exception{
    public ReactiveSocketException() {
    }

    public ReactiveSocketException(String message) {
        super(message);
    }

    public ReactiveSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReactiveSocketException(Throwable cause) {
        super(cause);
    }

    public ReactiveSocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
