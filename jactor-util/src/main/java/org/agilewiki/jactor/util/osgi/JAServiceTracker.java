package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.api.*;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JAServiceTracker<T> extends ActorBase implements ServiceListener, AutoCloseable {
    private final Logger log = LoggerFactory.getLogger(JAServiceTracker.class);
    private BundleContext bundleContext;
    private String listenerFilter;
    private String clazz;
    private HashMap<ServiceReference, T> tracked = new HashMap<ServiceReference, T>();
    private boolean started;
    private boolean closed;
    private ServiceChangeReceiver<T> serviceChangeReceiver;

    public JAServiceTracker(Mailbox _mailbox, String _clazz) throws Exception {
        initialize(_mailbox);
        bundleContext = MailboxFactoryActivator.getBundleContext(_mailbox.getMailboxFactory());
        listenerFilter = "(" + Constants.OBJECTCLASS + "="
                + _clazz.toString() + ")";
        clazz = _clazz;
    }

    public JAServiceTracker(Mailbox _mailbox, Filter _Filter) throws Exception {
        initialize(_mailbox);
        bundleContext = MailboxFactoryActivator.getBundleContext(_mailbox.getMailboxFactory());
        listenerFilter = _Filter.toString();
    }

    public Request<Map<ServiceReference, T>> startReq(final ServiceChangeReceiver<T> _serviceChangeReceiver) throws Exception {
        return new RequestBase<Map<ServiceReference, T>>(getMailbox()) {
            @Override
            public void processRequest(Transport<Map<ServiceReference, T>> _transport) throws Exception {
                if (started)
                    throw new IllegalStateException("already started");
                if (closed)
                    throw new IllegalStateException("closed");
                started = true;
                serviceChangeReceiver = _serviceChangeReceiver;
                bundleContext.addServiceListener(JAServiceTracker.this, listenerFilter);
                ServiceReference[] references = null;
                if (clazz == null) {
                    references = bundleContext.getServiceReferences(clazz, listenerFilter);
                } else {
                    references = bundleContext.getServiceReferences(clazz, null);
                }
                int i = 0;
                while (i < references.length) {
                    ServiceReference ref = references[i];
                    try {
                        T s = (T) bundleContext.getService(ref);
                        tracked.put(ref, s);
                    } catch (Exception exception) {
                    }
                    i += 1;
                }
                Map<ServiceReference, T> m = new HashMap<ServiceReference, T>(tracked);
                _transport.processResponse(m);
            }
        };
    }

    @Override
    public void close() {
        if (closed)
            return;
        closed = true;
        bundleContext.removeServiceListener(this);
    }

    @Override
    public final void serviceChanged(final ServiceEvent _event) {
        if (closed)
            return;
        try {
            new RequestBase<Void>(getMailbox()){
                @Override
                public void processRequest(Transport<Void> _transport) throws Exception {
                    int typ = _event.getType();
                    ServiceReference ref = _event.getServiceReference();
                    switch (typ) {
                        case ServiceEvent.REGISTERED :
                        case ServiceEvent.MODIFIED :
                            T s = (T) bundleContext.getService(ref);
                            tracked.put(ref, s);
                            break;
                        case ServiceEvent.MODIFIED_ENDMATCH :
                        case ServiceEvent.UNREGISTERING :
                            tracked.remove(ref);
                            break;
                    }
                    Map<ServiceReference, T> m = new HashMap<ServiceReference, T>(tracked);
                    new ServiceChange<T>(_event, m).signal(getMailbox(), serviceChangeReceiver);
                    _transport.processResponse(null);
                }
            }.signal();
        } catch (Exception exception) {
            log.error("Unable to signal", exception);
            try {
                getMailbox().getMailboxFactory().close();
            } catch (Exception e) {
            }
        }
    }
}
