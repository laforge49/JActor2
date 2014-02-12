import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

class Hanger extends NonBlockingBladeBase {
    Hanger() {
        super(new NonBlockingReactor());
    }

    SyncRequest<Void> looperSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() {
                while (true) {}
            }
        };
    }

    SyncRequest<Void> sleeperSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws InterruptedException {
                Thread.sleep(Long.MAX_VALUE);
                return null;
            }
        };
    }
}
