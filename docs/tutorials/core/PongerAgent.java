import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.SyncAgent;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public class PongerAgent extends BladeBase implements SyncAgent {
    private final Ponger ponger;
    private final long count;
    
    public static SyncRequest<Void> startSReq(
            final Ponger _ponger, 
            final long _count) throws Exception {
        return new PongerAgent(_ponger, _count).startSReq();
    }

    public PongerAgent(
        final Ponger _ponger, 
        final long _count) throws Exception {
        initialize(_ponger.getReactor());
        ponger = _ponger;
        count = _count;
    }
    
    public SyncRequest<Void> startSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                long i = 0;
                while (i < count) {
                    local(ponger.pingSReq());
                }
                return null;
            }
        };
    }
}
