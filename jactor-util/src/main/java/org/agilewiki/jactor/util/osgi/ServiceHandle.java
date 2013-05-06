package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.api.*;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public abstract class ServiceHandle<T> extends ActorBase implements ServiceListener {
    private final Logger log = LoggerFactory.getLogger(ServiceHandle.class);
    private BundleContext bundleContext;
    private String listenerFilter;
    private String clazz;
    private HashMap<ServiceReference, T> tracked = new HashMap<ServiceReference, T>();

    public ServiceHandle(Mailbox _mailbox, String _clazz) throws Exception {
        initialize(_mailbox);
        bundleContext = MailboxFactoryActivator.getBundleContext(_mailbox.getMailboxFactory());
        listenerFilter = "(" + Constants.OBJECTCLASS + "="
                + _clazz.toString() + ")";
        clazz = _clazz;
    }

    public ServiceHandle(Mailbox _mailbox, Filter _Filter) throws Exception {
        initialize(_mailbox);
        bundleContext = MailboxFactoryActivator.getBundleContext(_mailbox.getMailboxFactory());
        listenerFilter = _Filter.toString();
    }

    public Request<Void> startReq() throws Exception {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                bundleContext.addServiceListener(ServiceHandle.this, listenerFilter);
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
                _transport.processResponse(null);
            }
        };
    }

    @Override
    public final void serviceChanged(final ServiceEvent _event) {
        try {
            new RequestBase<Void>(getMailbox()){
                @Override
                public void processRequest(Transport<Void> _transport) throws Exception {
                    _serviceChanged(_event);
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

    protected void _serviceChanged(ServiceEvent event) {
        int typ = event.getType();
        ServiceReference ref = event.getServiceReference();
        switch (typ) {

        }
    }
}
