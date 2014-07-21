import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class ForeignPing extends NonBlockingBladeBase {
    private final Ponger ponger;

    public ForeignPing(final Ponger _ponger) throws Exception {
        ponger = _ponger;
    }
    
    public AOp<Void> pingAOp() {
        return new AOp<Void>("ping", getReactor()) {
            @Override
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
				AsyncResponseProcessor<Long> pongerResponseProcessor = 
						new AsyncResponseProcessor<Long>() {
					public void processAsyncResponse(final Long response) {
						_asyncResponseProcessor.processAsyncResponse(null);
					}
				};
            
                SyncRequest<Long> pingSOp = ponger.pingSOp();
                _asyncRequestImpl.send(pingSOp, pongerResponseProcessor);
            }
        };
    }
    
    public static void main(String[] args) throws Exception {
        new Plant();
        try {
            Ponger ponger = new Ponger();
            ForeignPing foreignPing = new ForeignPing(ponger);
            AOp<Void> pingAOp = foreignPing.pingAOp();
            pingAOp.call();
        } finally {
            Plant.close();
        }
    }
}
