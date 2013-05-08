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

/**
 * Imports a FactoryLocator from another bundle into the factory locator of the current bundle.
 */
public class FactoriesImporter
        extends ActorBase implements ServiceChangeReceiver<FactoryLocator> {

    /**
     * Logger for this object.
     */
    private final Logger log = LoggerFactory.getLogger(FactoriesImporter.class);

    /**
     * The service tracker used to find and then monitor the set of matching services.
     */
    private JAServiceTracker<FactoryLocator> tracker;

    /**
     * The transport for the start request. Once a match is found,
     * startTransport is set to null.
     */
    private Transport startTransport;

    /**
     * The factory locator of the current bundle.
     */
    private FactoryLocator factoryLocator;

    /**
     * Create and initialize a factories importer.
     *
     * @param _mailbox The mailbox of the factory locator actor.
     */
    public FactoriesImporter(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
        factoryLocator = Durables.getFactoryLocator(_mailbox.getMailboxFactory());
    }

    /**
     * Wait for a matching factory locator, unless one is already registered.
     * If more than one is found, reject the request.
     * But once a match has been made and the factory locator is imported,
     * any change to the set of matching factory locator's will stop the current bundle.
     *
     * @param _filter A filter that should identify the single factory locator to be imported into the
     *                factory locator of the current bundle.
     * @return The request.
     */
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
