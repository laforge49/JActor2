import org.agilewiki.jactor2.core.blades.SyncAgentBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public class PongerAgent extends SyncAgentBase<Void, Ponger> {
    private final long count;
    
    public static SyncRequest<Void> startSReq(
            final Ponger _ponger, 
            final long _count) throws Exception {
        return new PongerAgent(_ponger, _count).startSReq();
    }

    public PongerAgent(
            final Ponger _ponger, 
            final long _count) throws Exception {
        super(_ponger);
        count = _count;
    }
    
    @Override
    public Void start() throws Exception {
        long i = 0;
        while (i < count) {
            local(blade.pingSReq());
        }
        return null;
    }
}
