import org.agilewiki.jactor2.core.blades.SyncAgentBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;

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
    protected Void processSyncRequest() throws Exception {
        long i = 0;
        while (i < count) {
            i++;
            local(ponger.pingSReq());
        }
        return null;
    }
}
