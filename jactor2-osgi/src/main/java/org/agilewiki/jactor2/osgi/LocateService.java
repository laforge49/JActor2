package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.Mailbox;
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
    private Mailbox mailbox;

    /**
     * The service tracker actor.
     */
    private JAServiceTracker<T> tracker;

    /**
     * The transport for returning the service.
     */
    private Transport<T> transport;

    /**
     * Create a LocateService actor.
     *
     * @param _mailbox The actor processing.
     * @param clazz    Class name of the desired service.
     */
    public LocateService(Mailbox _mailbox, String clazz) throws Exception {
        mailbox = _mailbox;
        tracker = new JAServiceTracker(mailbox, clazz);
    }

    /**
     * Returns a request to locate the service.
     *
     * @return The request.
     */
    public Request<T> getReq() {
        return new Request<T>(mailbox) {
            @Override
            public void processRequest(final Transport<T> _transport) throws Exception {
                tracker.start(LocateService.this);
                transport = _transport;
            }
        };
    }

    @Override
    public void serviceChange(ServiceEvent _event,
                              Map<ServiceReference, T> _tracked)
            throws Exception {
        if (_tracked.size() > 0 && transport != null) {
            T service = _tracked.values().iterator().next();
            transport.processResponse(service);
            transport = null;
            tracker.close();
            tracker = null;
        }
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }
}
