import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.Delay;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class Parallel extends NonBlockingBladeBase {
    private final long count;
    
    public Parallel(final long _count)
            throws Exception {
        count = _count;
    }
    
    public AsyncRequest<Void> runAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncRequest<Void> dis = this;
            
            final AsyncResponseProcessor<Void> sleepResponseProcessor = 
                    new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    if (dis.getPendingResponseCount() == 0)
                        dis.processAsyncResponse(null);
                }
            };
            
            public void processAsyncRequest() throws Exception {
                long j = 0;
                while(j < count) {
                    j++;
                    Delay delay = new Delay();
                    send(delay.sleepSReq(100), sleepResponseProcessor);
                }
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        final long count = 10L;
        new Plant(10);
        try {
            Parallel parallel = new Parallel(count);
            AsyncRequest<Void> runAReq = parallel.runAReq();
            final long before = System.currentTimeMillis();
            runAReq.call();
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
