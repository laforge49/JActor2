import org.agilewiki.jactor2.core.blades.BlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class Batcher extends BlockingBladeBase {
    private final long count;
    private final Ponger ponger;
    
    public Batcher(final BlockingReactor _reactor, final long _count, final Ponger _ponger) {
        super(_reactor);
        count = _count;
        ponger = _ponger;
    }
    
    public AOp<Void> runAOp() {
        return new AOp<Void>("run", getReactor()) {
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
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
        final int count = 2000000;
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger(new BlockingReactor(1000, count));
            Batcher batcher = new Batcher(new BlockingReactor(1000, count), count, ponger);
            AOp<Void> runAOp = batcher.runAOp();
            final long before = System.nanoTime();
            runAOp.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Batch Timings", duration, count);
        } finally {
            plant.close();
        }
    }
}
