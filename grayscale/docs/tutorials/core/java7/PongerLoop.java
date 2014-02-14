import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class PongerLoop extends SyncRequest<Void> {
    private final Ponger ponger;
    private final long count;

    public PongerLoop(
            final Ponger _ponger, 
            final long _count) {
        super(_ponger.getReactor());
        ponger = _ponger;
        count = _count;
    }
    
    @Override
    public Void processSyncRequest() {
        long i = 0;
        Reactor reactor = getTargetReactor();
        while (i < count) {
            i++;
            ponger.ping(reactor);
        }
        return null;
    }
}
