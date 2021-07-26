package ws.prospeak.project.socket.reactivex.server.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.concurrent.Flow;

public class Pool  implements Flow.Subscriber<Connection> {
    private static final Logger log = LoggerFactory.
            getLogger(Pool.class);

    private Flow.Subscription pool;
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.pool = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(Connection connection) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}
