import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class DiningTable extends NonBlockingBladeBase {
    public final int seats;
    public final int meals;
    
    private int mealsEaten;
    private int[] forkUsage;
    private AsyncResponseProcessor<Boolean>[] pendingResponses;

    public DiningTable(final int _seats, final int _meals) 
            throws Exception {
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
    
    public AsyncRequest<Boolean> eatAReq(final int _seat) {
        return new AsyncBladeRequest<Boolean>() {
            final AsyncResponseProcessor<Boolean> dis = this;
            
            @Override
            public void processAsyncRequest() throws Exception {
                setNoHungRequestCheck(); //inhibit the test for hung request
                
                if (mealsEaten == meals) {
                    dis.processAsyncResponse(false);
                    return;
                }
                
                if (getForks(_seat)) {
                    chowTime(_seat);
                    dis.processAsyncResponse(true);
                    return;
                }
                
                pendingResponses[_seat] = dis;
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
    
    public SyncRequest<Void> ateSReq(final int _seat) {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
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