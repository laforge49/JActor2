package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.mt.mtPlant.PlantScheduler;
import org.agilewiki.jactor2.core.plant.PlantBase;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

import java.util.concurrent.ScheduledFuture;

/**
 * Returns a result after a delay.
 * Builds on PlantScheduler.
 */
public class DelayAReq extends AsyncRequest<Void> {
    private final long millisecondDelay;
    private ScheduledFuture<?> scheduledFuture;
    private AsyncResponseProcessor<Void> dis = this;

    /**
     * Create a DelayAReq.
     *
     * @param _millisecondDelay    How long to wait before responding.
     */
    public DelayAReq(long _millisecondDelay) {
        super(PlantBase.getInternalReactor());
        millisecondDelay = _millisecondDelay;
    }

    /**
     * Closes the scheduled future when the request is canceled.
     */
    @Override
    public void onCancel() {
        scheduledFuture.cancel(false);
    }

    /**
     * Closes the scheduled future when the request is closed.
     */
    @Override
    public void onClose() {
        scheduledFuture.cancel(false);
    }

    @Override
    public void processAsyncRequest() {
        setNoHungRequestCheck();
        PlantScheduler plantScheduler = Plant.getPlantScheduler();
        Runnable runnable = new Runnable() {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        scheduledFuture = plantScheduler.schedule(runnable, millisecondDelay);
        Reactor sourceReactor = getSourceReactor();
    }
}
