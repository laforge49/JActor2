import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Ponger2 extends NonBlockingBladeBase {
    private long count = 0;
    
    public Ponger2(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    public long ping(final Reactor _sourceReactor) {
        if (getReactor() != _sourceReactor)
            throw new UnsupportedOperationException("reactors are not the same");
        count += 1;
        return count;
    }

    public SyncRequest pingSReq() {
        return new SyncBladeRequest() {
            @Override
            public Long processSyncRequest() throws Exception {
                return ping(getSourceReactor());
            }
        };
    }
}
