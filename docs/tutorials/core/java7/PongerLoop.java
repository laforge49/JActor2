import org.agilewiki.jactor2.core.requests.SyncRequest;

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
        while (i < count) {
            i++;
            local(ponger.pingSReq());
        }
        return null;
    }
}
