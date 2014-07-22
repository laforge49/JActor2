import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class DiningTable extends NonBlockingBladeBase {
    public final int seats;
    public final int meals;
    
    private int mealsEaten;
    private int[] forkUsage;
    private AsyncResponseProcessor<Boolean>[] pendingResponses;

    public DiningTable(final int _seats, final int _meals) throws Exception {
        seats = _seats;
        meals = _meals;
        forkUsage = new int[seats];
        int i = 0;
        while (i < seats) {
            forkUsage[i] = -1;
            i++;
        }
        pendingResponses = new AsyncResponseProcessor[seats];
    }
    
    private int leftFork(final int _seat) {
        return _seat;
    }
    
    private int rightFork(final int _seat) {
        return (_seat + 1) % seats;
    }
    
    private boolean isForkAvailable(final int _seat) {
        return forkUsage[_seat] == -1;
    }
    
    private boolean getForks(final int _seat) {
        int leftFork = leftFork(_seat);
        int rightFork = rightFork(_seat);
        if (isForkAvailable(leftFork) && isForkAvailable(rightFork)) {
            forkUsage[leftFork] = _seat;
            forkUsage[rightFork] = _seat;
            return true;
        }
        return false;
    }
    
    public AOp<Boolean> eatAOp(final int _seat) {
        return new AOp<Boolean>("eat", getReactor()) {
            @Override
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Boolean> _asyncResponseProcessor) throws Exception {
                _asyncRequestImpl.setNoHungRequestCheck(); //inhibit the test for hung request
                
                if (mealsEaten == meals) {
                    _asyncResponseProcessor.processAsyncResponse(false);
                    return;
                }
                
                if (getForks(_seat)) {
                    chowTime(_seat);
                    _asyncResponseProcessor.processAsyncResponse(true);
                    return;
                }
                
                pendingResponses[_seat] = _asyncResponseProcessor;
            }
        };
    }
    
    private void chowTime(final int _seat) throws Exception {
        mealsEaten++;
        if (mealsEaten == meals) {
            int i = 0;
            while (i < seats) {
                AsyncResponseProcessor<Boolean> pendingResponse = pendingResponses[i];
                if (pendingResponse != null) {
                    pendingResponse.processAsyncResponse(false);
                }
                i++;
            }
        }
    }
    
    private int leftSeat(final int _fork) {
        return (_fork + seats - 1) % seats;
    }
    
    private int rightSeat(final int _fork) {
        return _fork;
    }

    private void notice(final int _seat) throws Exception {
        AsyncResponseProcessor<Boolean> pendingResponse = pendingResponses[_seat];
        if (pendingResponse == null)
            return;
        if (!getForks(_seat))
            return;
        if (mealsEaten < meals) {
            pendingResponses[_seat] = null;
            chowTime(_seat);
            pendingResponse.processAsyncResponse(true);
        }
    }
    
    public SOp<Void> ateSOp(final int _seat) {
        return new SOp<Void>("ate", getReactor()) {
            @Override
            public Void processSyncOperation(final RequestImpl _requestImpl) throws Exception {
                int leftFork = leftFork(_seat);
                int rightFork = rightFork(_seat);
                forkUsage[leftFork] = -1;
                forkUsage[rightFork] = -1;
                notice(leftSeat(leftFork));
                notice(rightSeat(rightFork));
                return null;
            }
        };
    }
}