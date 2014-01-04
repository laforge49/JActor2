import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Ponger extends NonBlockingBladeBase {
    private long count = 0;
    
    public Ponger() throws Exception {}

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
            public Long processSyncRequest() throws Exception {
                return ping();
            }
        };
    }
}
