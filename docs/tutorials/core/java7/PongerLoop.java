import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class PongerLoop extends SyncRequest<Void> {
    private final Ponger ponger;
    private final long count;

    public PongerLoop(
            final Ponger _ponger, 
            final long _count) throws Exception {
        super(_ponger.getReactor());
        ponger = _ponger;
        count = _count;
    }
    
    @Override
    public Void processSyncRequest() throws Exception {
        long i = 0;
        Reactor reactor = getTargetReactor();
        while (i < count) {
            i++;
            ponger.ping(reactor);
        }
        return null;
    }
}
