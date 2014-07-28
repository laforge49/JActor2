import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.SAOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class Pinger extends NonBlockingBladeBase {
    private final Ponger ponger;
    private final SOp<Long> pingSOp;

    public Pinger(final NonBlockingReactor _reactor, final Ponger _ponger) {
        super(_reactor);
        ponger = _ponger;
        pingSOp = ponger.pingSOp();
    }

    public AOp<Void> loopAOp(final long _count) {
        return new SAOp<Void>("loop", getReactor()) {
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
			public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl) throws Exception {
                iterate();
            }
            
            public void iterate() throws Exception {
                if (i >= _count) {
                    processAsyncResponse(null);
                    return;
                }
                getAsyncRequestImpl().send(pingSOp, pingResponseProcessor);
            }
        };
    }
}
