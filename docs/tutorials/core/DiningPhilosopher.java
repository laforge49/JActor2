import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class DiningPhilosopher extends BladeBase {
    public DiningPhilosopher(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<Integer> feastAReq(final DiningTable _diningTable, final int _seat)
            throws Exception {
        return new AsyncBladeRequest<Integer>() {
            final AsyncResponseProcessor<Integer> dis = this;
            
            private int mealsEaten;
            private AsyncResponseProcessor<Void> ateResponseProcessor;
            private AsyncResponseProcessor<Boolean> eatResponseProcessor;
            
            @Override
            protected void processAsyncRequest() throws Exception {
                ateResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _ateResponse) throws Exception {
                        AsyncRequest<Boolean> eatAReq = _diningTable.eatAReq(_seat);
                        send(eatAReq, eatResponseProcessor);
                    }
                };
                eatResponseProcessor = new AsyncResponseProcessor<Boolean>() {
                    @Override
                    public void processAsyncResponse(final Boolean _eatResponse) throws Exception {
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
