import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Ponger extends BladeBase {
    private long count = 0;
    
    public Ponger(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    SyncRequest<Long> pingSReq() {
        return new SyncRequest<Long>(getReactor()) {
            @Override
            public Long processSyncRequest() throws Exception {
                count += 1;
                return count;
            }
        };
    }
}
