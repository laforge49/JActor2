import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class Pinger extends NonBlockingBladeBase {
    private final Ponger ponger;

    public Pinger(final NonBlockingReactor _reactor, final Ponger _ponger) {
        super(_reactor);
        ponger = _ponger;
    }

    public AOp<Void> loopAOp(final long _count) {
        return new AOp<Void>("loop", getReactor()) {
            long i = 0;

			@Override
			public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
				final AsyncResponseProcessor<Long> pingResponseProcessor = 
						new AsyncResponseProcessor<Long>() {
					@Override
					public void processAsyncResponse(final Long _response) {
						i++;
						iterate(_asyncRequestImpl, _asyncResponseProcessor);
					}
				};

                iterate(_asyncRequestImpl, _asyncResponseProcessor);
            }
            
            public void iterate(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                if (i >= _count) {
                    _asyncResponseProcessor.processAsyncResponse(null);
                    return;
                }
                SOp<Long> pingSOp = ponger.pingSOp();
                _asyncRequestImpl.send(pingSOp, pingResponseProcessor);
            }
        };
    }
}
