import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Batcher extends NonBlockingBladeBase {
    private final long count;
    private final Ponger ponger;
    
    public Batcher(final NonBlockingReactor _reactor, final long _count, final Ponger _ponger)
            throws Exception {
        super(_reactor);
        count = _count;
        ponger = _ponger;
    }
    
    public AsyncRequest<Void> runAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;
            
            final AsyncResponseProcessor<Long> pingResponseProcessor = 
                new AsyncResponseProcessor<Long>() {
                
                long i = 0;
                
                @Override
                public void processAsyncResponse(final Long _response) throws Exception {
                    i++;
                    if (i == count)
                        dis.processAsyncResponse(null);
                }
            };
            
            public void processAsyncRequest() throws Exception {
                long j = 0;
                while(j < count) {
                    j++;
                    send(ponger.pingSReq(), pingResponseProcessor);
                }
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        final long count = 1000000L;
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor());
            Batcher batcher = new Batcher(new NonBlockingReactor(), count, ponger);
            AsyncRequest<Void> runAReq = batcher.runAReq();
            final long before = System.nanoTime();
            runAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Batch Timings", duration, count);
        } finally {
            plant.close();
        }
    }
}
