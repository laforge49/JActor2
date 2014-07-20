import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class DiningPhilosopher extends NonBlockingBladeBase {
	public DiningPhilosopher() throws Exception {
	}
	
    public AOp<Integer> feastAOp(final DiningTable _diningTable, final int _seat) {
        return new AOp<Integer>("feast", getReactor()) {
            private int mealsEaten;
            private AsyncResponseProcessor<Void> ateResponseProcessor;
            private AsyncResponseProcessor<Boolean> eatResponseProcessor;
            
            @Override
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                ateResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _ateResponse) {
                        AsyncRequest<Boolean> eatAOp = _diningTable.eatAOp(_seat);
                        _asyncRequestImpl.send(eatAOp, eatResponseProcessor);
                    }
                };
				
                eatResponseProcessor = new AsyncResponseProcessor<Boolean>() {
                    @Override
                    public void processAsyncResponse(final Boolean _eatResponse) {
                        if (!_eatResponse) {
                            _asyncResponseProcessor.processAsyncResponse(mealsEaten);
                            return;
                        }
                        mealsEaten++;
                        SOp<Void> ateSReq = _diningTable.ateSOp(_seat);
                        _asyncRequestImpl.send(ateSReq, ateResponseProcessor);
                    }
                };
				
                AOp<Boolean> eatAOp = _diningTable.eatAOp(_seat);
                _asyncRequestImpl.send(eatAOp, eatResponseProcessor);
            }
        };
    }
}
