import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Batcher extends BladeBase {
    private final long count;
    private final Ponger ponger;
    
    public Batcher(final NonBlockingReactor _reactor, final long _count, final Ponger _ponger)
            throws Exception {
        initialize(_reactor);
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
            
            protected void processAsyncRequest() throws Exception {
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
        Facility facility = new Facility();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(facility));
            Batcher batcher = new Batcher(new NonBlockingReactor(facility), count, ponger);
            AsyncRequest<Void> runAReq = batcher.runAReq();
            final long before = System.nanoTime();
            runAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            Printer printer = Printer.stdoutAReq(facility).call();
            SpeedReport.startSReq(printer, "Batch Timings", duration, count).call();
        } finally {
            facility.close();
        }
    }
}
