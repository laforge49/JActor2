import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class ForeignPing extends NonBlockingBladeBase {
    private final Ponger ponger;

    public ForeignPing(final Ponger _ponger) throws Exception {
        ponger = _ponger;
    }
    
    public AsyncRequest<Void> pingAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor<Void> dis = this;
        
            AsyncResponseProcessor<Long> pongerResponseProcessor = 
                    new AsyncResponseProcessor<Long>() {
                public void processAsyncResponse(final Long response) throws Exception {
                    dis.processAsyncResponse(null);
                }
            };
            
            public void processAsyncRequest() throws Exception {
                SyncRequest<Long> pingSReq = ponger.pingSReq();
                send(pingSReq, pongerResponseProcessor);
            }
        };
    }
    
    public static void main(String[] args) throws Exception {
        new Plant();
        try {
            Ponger ponger = new Ponger();
            ForeignPing foreignPing = new ForeignPing(ponger);
            AsyncRequest<Void> pingAReq = foreignPing.pingAReq();
            pingAReq.call();
        } finally {
            Plant.close();
        }
    }
}
