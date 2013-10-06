import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Loop extends BladeBase {

    public Loop(final Reactor _reactor) throws Exception {
        initialize(_reactor);
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
                Facility myFacility = getReactor().getFacility();
                AsyncRequest<Void> printCount = Printer.printlnAReq(myFacility, String.valueOf(i));
                send(printCount, printCountResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Loop loop = new Loop(new NonBlockingReactor(facility));
            AsyncRequest<Void> loopAReq = loop.loopAReq(10L);
            loopAReq.call();
        } finally {
            facility.close();
        }
    }
}
