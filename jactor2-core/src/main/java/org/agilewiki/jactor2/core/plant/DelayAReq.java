package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * Returns a result after a delay.
 * Builds on PlantScheduler.
 */
public class DelayAReq extends AsyncRequest<Void> {
    private final int millisecondDelay;
    private Object scheduledFuture;
    private final AsyncResponseProcessor<Void> dis = this;

    /**
     * Create a DelayAReq.
     *
     * @param _millisecondDelay    How long to wait before responding.
     */
    public DelayAReq(final int _millisecondDelay) {
        super(PlantBase.getInternalFacility());
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
                    new SOp<Void>("timeout", PlantBase.getInternalFacility()) {
                        @Override
                        public Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                            if (!isCanceled()) {
                                dis.processAsyncResponse(null);
                            }
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
