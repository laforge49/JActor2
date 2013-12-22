import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Loop extends NonBlockingBladeBase {

    public Loop(final NonBlockingReactor _reactor) throws Exception {
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
            public void processAsyncRequest() throws Exception {
                iterate();
            }
            
            public void iterate() throws Exception {
                if (i >= _count) {
                    dis.processAsyncResponse(null);
                    return;
                }
                i++;
                Plant myPlant = getReactor().getPlant();
                AsyncRequest<Void> printCount = Printer.printlnAReq(myPlant, String.valueOf(i));
                send(printCount, printCountResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            Loop loop = new Loop(new NonBlockingReactor(plant));
            AsyncRequest<Void> loopAReq = loop.loopAReq(10L);
            loopAReq.call();
        } finally {
            plant.close();
        }
    }
}
