package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.closeable.FutureCloser;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

import java.util.concurrent.ScheduledFuture;

/**
 * Returns a result after a delay.
 * Builds on PlantScheduler.
 */
public class DelayAReq extends AsyncRequest<Void> {
    private final long millisecondDelay;
    private FutureCloser futureCloser;
    private ScheduledFuture<?> scheduledFuture;
    private boolean canceled;

    /**
     * Create a DelayAReq.
     *
     * @param _millisecondDelay    How long to wait before the Runnable is to be run.
     */
    public DelayAReq(long _millisecondDelay) {
        super(Plant.getInternalReactor());
        millisecondDelay = _millisecondDelay;
    }

    /**
     * Cancel the response if not yet sent.
     */
    public void cancel() throws Exception {
        canceled = true;
        if (futureCloser != null)
            futureCloser.close();
        else
            scheduledFuture.cancel(false);
    }

    /**
     * Returns true if the response was canceled.
     *
     * @return True if the response was canceled.
     */
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void processAsyncRequest() throws Exception {
        PlantScheduler plantScheduler = Plant.getPlantScheduler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (futureCloser != null)
                        futureCloser.close();
                    processAsyncResponse(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        scheduledFuture = plantScheduler.schedule(runnable, millisecondDelay);
        Reactor sourceReactor = getSourceReactor();
        if (sourceReactor != null)
            futureCloser = new FutureCloser(scheduledFuture);
    }
}
