package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SAOp;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * Returns a result after a delay.
 * Builds on PlantScheduler.
 */
public class DelayAOp extends SAOp<Void> {
    private final int millisecondDelay;
    private Object scheduledFuture;
    private final AsyncResponseProcessor<Void> dis = this;

    /**
     * Create a DelayAReq.
     *
     * @param _millisecondDelay    How long to wait before responding.
     */
    public DelayAOp(final int _millisecondDelay) {
        super("Delay", PlantBase.getInternalFacility());
        millisecondDelay = _millisecondDelay;
    }

    /**
     * Closes the scheduled future when the request is canceled.
     */
    @Override
    public void onCancel(final AsyncRequestImpl _asyncRequestImpl) {
        PlantBase.getPlantScheduler().cancel(scheduledFuture);
    }

    /**
     * Closes the scheduled future when the request is closed.
     */
    @Override
    public void onClose(final AsyncRequestImpl _asyncRequestImpl) {
        onCancel(_asyncRequestImpl);
    }

    @Override
    protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl) throws Exception {
        _asyncRequestImpl.setNoHungRequestCheck();
        final PlantScheduler plantScheduler = PlantBase.getPlantScheduler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    new SOp<Void>("timeout", PlantBase.getInternalFacility()) {
                        @Override
                        public Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                            if (!_asyncRequestImpl.isCanceled()) {
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
