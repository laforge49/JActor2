import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Loop extends NonBlockingBladeBase {

    public Loop(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    public AsyncRequest<Void> loopAReq(final long _count) throws Exception {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;
            long i = 0;
            final Delay delay = new Delay();

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
                System.out.println(String.valueOf(i));
                send(delay.sleepSReq(1), printCountResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            Loop loop = new Loop(new NonBlockingReactor());
            AsyncRequest<Void> loopAReq = loop.loopAReq(10L);
            loopAReq.call();
        } finally {
            plant.close();
        }
    }
}
