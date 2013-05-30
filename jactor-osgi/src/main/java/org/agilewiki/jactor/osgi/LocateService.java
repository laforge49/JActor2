package org.agilewiki.jactor.osgi;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import java.util.Map;

/**
 * A simplified service tracker.
 */
public class LocateService<T> implements ServiceChangeReceiver<T> {
    private Mailbox mailbox;
    JAServiceTracker<T> tracker;
    Transport<T> transport;

    public LocateService(Mailbox _mailbox, String clazz) throws Exception {
        mailbox = _mailbox;
        tracker = new JAServiceTracker(mailbox, clazz);
    }

    public Request<T> getReq() {
        return new RequestBase<T>(mailbox) {
            @Override
            public void processRequest(final Transport<T> _transport) throws Exception {
                tracker.startReq(LocateService.this).signal(mailbox);
                transport = _transport;
            }
        };
    }

    @Override
    public void serviceChange(ServiceEvent _event,
                              Map<ServiceReference, T> _tracked,
                              Transport _transport)
            throws Exception {
        _transport.processResponse(null);
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
