import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Loop extends BladeBase {
    private final Printer printer;

    public Loop(final Reactor _reactor, final Printer _printer) throws Exception {
        initialize(_reactor);
        printer = _printer;
    }

    public AsyncRequest<Void> loopAReq(final long _count) {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;
            long i = 0;

            final AsyncResponseProcessor<Void> printCountResponseProcessor = 
                    new AsyncResponseProcessor<Void>() {

                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    iterate();
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                iterate();
            }
            
            public void iterate() throws Exception {
                if (i >= _count) {
                    dis.processAsyncResponse(null);
                    return;
                }
                i++;
                SyncRequest<Void> printCount = printer.printlnSReq(String.valueOf(i));
                send(printCount, printCountResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Printer printer = new Printer(new IsolationReactor(facility));
            Loop loop = new Loop(
                new NonBlockingReactor(facility),
                printer);
            AsyncRequest<Void> loopAReq = loop.loopAReq(10L);
            loopAReq.call();
        } finally {
            facility.close();
        }
    }
}
