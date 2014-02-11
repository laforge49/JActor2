import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class DiningPhilosopher extends NonBlockingBladeBase {
    public AsyncRequest<Integer> feastAReq(final DiningTable _diningTable, final int _seat) {
        return new AsyncBladeRequest<Integer>() {
            final AsyncRequest<Integer> dis = this;
            
            private int mealsEaten;
            private AsyncResponseProcessor<Void> ateResponseProcessor;
            private AsyncResponseProcessor<Boolean> eatResponseProcessor;
            
            @Override
            public void processAsyncRequest() {
                ateResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _ateResponse) {
                        AsyncRequest<Boolean> eatAReq = _diningTable.eatAReq(_seat);
                        send(eatAReq, eatResponseProcessor);
                    }
                };
                eatResponseProcessor = new AsyncResponseProcessor<Boolean>() {
                    @Override
                    public void processAsyncResponse(final Boolean _eatResponse) {
                        if (!_eatResponse) {
                            dis.processAsyncResponse(mealsEaten);
                            return;
                        }
                        mealsEaten++;
                        SyncRequest<Void> ateSReq = _diningTable.ateSReq(_seat);
                        send(ateSReq, ateResponseProcessor);
                    }
                };
                AsyncRequest<Boolean> eatAReq = _diningTable.eatAReq(_seat);
                send(eatAReq, eatResponseProcessor);
            }
        };
    }
}
