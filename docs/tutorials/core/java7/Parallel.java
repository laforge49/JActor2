import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Delay;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class Parallel extends NonBlockingBladeBase {
    private final long count;
    
    public Parallel(final long _count) throws Exception {
        count = _count;
    }
    
    public AOp<Void> runAOp() {
        return new AOp<Void>("run", getReactor()) {
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
				final AsyncResponseProcessor<Void> sleepResponseProcessor = 
						new AsyncResponseProcessor<Void>() {
					@Override
					public void processAsyncResponse(final Void _response) throws Exception {
						if (_asyncRequestImpl.getPendingResponseCount() == 0)
							_asyncResponseProcessor.processAsyncResponse(null);
					}
				};
            
                long j = 0;
                while(j < count) {
                    j++;
                    Delay delay = new Delay();
                    _asyncRequestImpl.send(delay.sleepSOp(100), sleepResponseProcessor);
                }
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        final long count = 10L;
        new Plant(10);
        try {
            Parallel parallel = new Parallel(count);
            AOp<Void> runAOp = parallel.runAOp();
            final long before = System.currentTimeMillis();
            runAOp.call();
            final long after = System.currentTimeMillis();
            final long duration = after - before;
            System.out.println("Parallel Test with 10 Threads");
            System.out.println("count: " + count);
            System.out.println("sleep duration: 100 milliseconds");
            System.out.println("total time: " + duration + " milliseconds");
        } finally {
            Plant.close();
        }
    }
}
