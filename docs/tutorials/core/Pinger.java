import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Pinger extends BladeBase {
    private final Ponger ponger;

    public Pinger(final Reactor _reactor, final Ponger _ponger) throws Exception {
        _initialize(_reactor);
        ponger = _ponger;
    }

    public AsyncRequest<Void> loopAReq(final long _count) {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;
            long i = 0;

            final AsyncResponseProcessor<Long> pingResponseProcessor = 
                    new AsyncResponseProcessor<Long>() {

                @Override
                public void processAsyncResponse(final Long _response) throws Exception {
                    i++;
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
                SyncRequest<Long> ping = ponger.pingSReq();
                send(ping, pingResponseProcessor);
            }
        };
    }
}
