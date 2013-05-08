package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.osgi.serviceTracker.JAServiceTracker;
import org.agilewiki.jactor.util.osgi.serviceTracker.ServiceChangeReceiver;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FactoriesImporter
        extends ActorBase implements ServiceChangeReceiver<FactoryLocator> {

    private final Logger log = LoggerFactory.getLogger(FactoriesImporter.class);
    private JAServiceTracker<FactoryLocator> tracker;
    private Transport startTransport;
    private FactoryLocator factoryLocator;

    public FactoriesImporter(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
        factoryLocator = Durables.getFactoryLocator(_mailbox.getMailboxFactory());
    }

    Request<Void> startReq(final Filter _filter) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Void> _transport) throws Exception {
                if (tracker != null)
                    throw new IllegalStateException("already started");
                tracker = new JAServiceTracker<FactoryLocator>(getMailbox(), _filter);
                startTransport = _transport;
                tracker.startReq(FactoriesImporter.this).send(
                        getMailbox(),
                        new ResponseProcessor<Map<ServiceReference, FactoryLocator>>() {
                    @Override
                    public void processResponse(Map<ServiceReference, FactoryLocator> response) throws Exception {
                        if (response.size() > 1) {
                            tracker.close();
                            tracker = null;
                            startTransport = null;
                            throw new IllegalStateException("ambiguous filter--number of matches = " + response.size());
                        }
                        if (response.size() == 1) {
                            FactoryLocator fl = response.values().iterator().next();
                            ((FactoryLocatorImpl) factoryLocator).importFactories(fl);
                            startTransport = null;
                            _transport.processResponse(null);
                        }
                    }
                });
            }
        };
    }

    @Override
    public void serviceChange(ServiceEvent _event,
                              Map<ServiceReference,
                                      FactoryLocator> _tracked,
                              Transport _transport) throws Exception {
        if (startTransport == null) {
            log.error("Unexpected service change");
            getMailbox().getMailboxFactory().close();
            return;
        }
        if (_tracked.size() > 1) {
            tracker.close();
            tracker = null;
            startTransport.processResponse(
                    new IllegalStateException("ambiguous filter--number of matches = " + _tracked.size()));
            startTransport = null;
            return;
        }
        if (_tracked.size() == 1) {
            FactoryLocator fl = _tracked.values().iterator().next();
            ((FactoryLocatorImpl) factoryLocator).importFactories(fl);
            startTransport.processResponse(null);
            startTransport = null;
            return;
        }
        log.info("strange case");
        _transport.processResponse(null);
    }
}
