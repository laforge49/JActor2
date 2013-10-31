package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import java.util.Map;

/**
 * Locates (or waits for) a service.
 */
public class LocateService<T> implements ServiceChangeReceiver<T> {

    /**
     * The processing.
     */
    private Reactor reactor;

    /**
     * The service tracker blades.
     */
    private JAServiceTracker<T> tracker;

    /**
     * The responseProcessor for returning the service.
     */
    private AsyncResponseProcessor<T> responseProcessor;

    /**
     * Create a LocateService blades.
     *
     * @param _reactor The blades processing.
     * @param clazz    Class name of the desired service.
     */
    public LocateService(Reactor _reactor, String clazz) throws Exception {
        reactor = _reactor;
        tracker = new JAServiceTracker(reactor, clazz);
    }

    /**
     * Returns a request to locate the service.
     *
     * @return The request.
     */
    public AsyncRequest<T> getReq() {
        return new AsyncRequest<T>(reactor) {
            @Override
            protected void processAsyncRequest() throws Exception {
                tracker.start(LocateService.this);
                responseProcessor = this;
            }
        };
    }

    @Override
    public void serviceChange(ServiceEvent _event,
                              Map<ServiceReference, T> _tracked)
            throws Exception {
        if (_tracked.size() > 0 && responseProcessor != null) {
            T service = _tracked.values().iterator().next();
            responseProcessor.processAsyncResponse(service);
            responseProcessor = null;
            tracker.close();
            tracker = null;
        }
    }

    @Override
    public Reactor getReactor() {
        return reactor;
    }

    abstract public class SyncBladeRequest<RESPONSE_TYPE> extends SyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public SyncBladeRequest() {
            super(getReactor());
        }
    }

    abstract public class AsyncBladeRequest<RESPONSE_TYPE> extends AsyncRequest<RESPONSE_TYPE> {

        /**
         * Create a SyncRequest.
         */
        public AsyncBladeRequest() {
            super(getReactor());
        }
    }

    /**
     * Process the request immediately.
     *
     * @param _syncRequest    The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     * @return The response from the request.
     */
    protected <RESPONSE_TYPE> RESPONSE_TYPE local(final SyncRequest<RESPONSE_TYPE> _syncRequest) throws Exception {
        return SyncRequest.doLocal(getReactor(), _syncRequest);
    }
}
