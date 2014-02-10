import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Ponger extends BladeBase {
    private long count = 0;
    
    public Ponger() {
        _initialize(new NonBlockingReactor());
    }
    
    public Ponger(final Reactor _reactor) {
        _initialize(_reactor);
    }

    private long ping() {
        count += 1;
        return count;
    }

    //Directly callable
    public long ping(final Reactor _sourceReactor) {
        directCheck(_sourceReactor);
        return ping();
    }

    public SyncRequest<Long> pingSReq() {
        return new SyncBladeRequest() {
            @Override
            public Long processSyncRequest() {
                return ping();
            }
        };
    }
}
