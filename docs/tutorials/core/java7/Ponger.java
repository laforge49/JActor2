import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Ponger extends NonBlockingBladeBase {
    private long count = 0;
    
    public Ponger(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    public SyncRequest<Long> pingSReq() {
        return new SyncBladeRequest<Long>() {
            @Override
            public Long processSyncRequest() throws Exception {
                count += 1;
                return count;
            }
        };
    }
}
