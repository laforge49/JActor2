import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Parallel extends BladeBase {
    private final long count;
    
    public Parallel(final NonBlockingReactor _reactor, final long _count)
            throws Exception {
        initialize(_reactor);
        count = _count;
    }
    
    public AsyncRequest<Void> runAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;
            
            final AsyncResponseProcessor<Void> sleepResponseProcessor = 
                new AsyncResponseProcessor<Void>() {
                
                long i = 0;
                
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    i++;
                    if (i == count)
                        dis.processAsyncResponse(null);
                }
            };
            
            protected void processAsyncRequest() throws Exception {
                Reactor reactor = getReactor();
                Facility facility = reactor.getFacility();
                long j = 0;
                while(j < count) {
                    j++;
                    Delay delay = new Delay(new IsolationReactor(facility));
                    send(delay.sleepSReq(100), sleepResponseProcessor);
                }
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        final long count = 10L;
        Facility facility = new Facility(10);
        try {
            Parallel parallel = new Parallel(new NonBlockingReactor(facility), count);
            AsyncRequest<Void> runAReq = parallel.runAReq();
            final long before = System.currentTimeMillis();
            runAReq.call();
            final long after = System.currentTimeMillis();
            final long duration = after - before;
            Printer printer = Printer.stdoutSReq(facility).call();
            printer.printlnSReq("Parallel Test with 10 Threads").call();
            printer.printlnSReq("count: " + count).call();
            printer.printlnSReq("sleep duration: 100 milliseconds").call();
            printer.printlnSReq("total time: " + duration + " milliseconds").call();
        } finally {
            facility.close();
        }
    }
}
