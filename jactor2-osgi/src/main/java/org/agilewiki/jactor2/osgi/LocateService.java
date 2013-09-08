package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
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
    private MessageProcessor messageProcessor;

    /**
     * The service tracker actor.
     */
    private JAServiceTracker<T> tracker;

    /**
     * The responseProcessor for returning the service.
     */
    private ResponseProcessor<T> responseProcessor;

    /**
     * Create a LocateService actor.
     *
     * @param _messageProcessor The actor processing.
     * @param clazz             Class name of the desired service.
     */
    public LocateService(MessageProcessor _messageProcessor, String clazz) throws Exception {
        messageProcessor = _messageProcessor;
        tracker = new JAServiceTracker(messageProcessor, clazz);
    }

    /**
     * Returns a request to locate the service.
     *
     * @return The request.
     */
    public AsyncRequest<T> getReq() {
        return new AsyncRequest<T>(messageProcessor) {
            @Override
            public void processRequest() throws Exception {
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
            responseProcessor.processResponse(service);
            responseProcessor = null;
            tracker.close();
            tracker = null;
        }
    }

    @Override
    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }
}
