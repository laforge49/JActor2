import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class Batcher extends NonBlockingBladeBase {
    private final long count;
    private final Ponger ponger;
    
    public Batcher(final NonBlockingReactor _reactor, final long _count, final Ponger _ponger) {
        super(_reactor);
        count = _count;
        ponger = _ponger;
    }
    
    public AOp<Void> runAOp() {
        return new AOp<Void>("run", getReactor()) {
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
                    final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                final AsyncResponseProcessor<Long> pingResponseProcessor = 
                        new AsyncResponseProcessor<Long>() {
                    long i = 0;
                
                    @Override
                    public void processAsyncResponse(final Long _response) throws Exception {
                        i++;
                        if (i == count)
                            _asyncResponseProcessor.processAsyncResponse(null);
                    }
                };
            
                SOp<Long> pingSOp = ponger.pingSOp();
                long j = 0;
                while(j < count) {
                    j++;
                    _asyncRequestImpl.send(pingSOp, pingResponseProcessor);
                }
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        final int count = 10000;
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(10000, count));
            Batcher batcher = new Batcher(new NonBlockingReactor(10000, count), count, ponger);
            AOp<Void> runAOp = batcher.runAOp();
            final long before = System.nanoTime();
            for (int i = 0; i < 10000; i++) 
                runAOp.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Batch Timings", duration, count * 10000);
        } finally {
            plant.close();
        }
    }
}
