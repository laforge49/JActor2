package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Returns a result after a delay.
 * Builds on PlantScheduler.
 */
public class DelayAReq extends AsyncRequest<Void> {
    private final long millisecondDelay;
    private Object scheduledFuture;
    private final AsyncResponseProcessor<Void> dis = this;

    /**
     * Create a DelayAReq.
     *
     * @param _millisecondDelay    How long to wait before responding.
     */
    public DelayAReq(final long _millisecondDelay) {
        super(PlantBase.getInternalReactor());
        millisecondDelay = _millisecondDelay;
    }

    /**
     * Closes the scheduled future when the request is canceled.
     */
    @Override
    public void onCancel() {
        PlantBase.getPlantScheduler().cancel(scheduledFuture);
    }

    /**
     * Closes the scheduled future when the request is closed.
     */
    @Override
    public void onClose() {
        onCancel();
    }

    @Override
    public void processAsyncRequest() {
        setNoHungRequestCheck();
        final PlantScheduler plantScheduler = PlantBase.getPlantScheduler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    new SyncRequest<Void>(PlantBase.getInternalReactor()) {
                        @Override
                        public Void processSyncRequest() throws Exception {
                            if (!isCanceled())
                                dis.processAsyncResponse(null);
                            return null;
                        }
                    }.signal();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        };
        scheduledFuture = plantScheduler.schedule(runnable, millisecondDelay);
    }
}
