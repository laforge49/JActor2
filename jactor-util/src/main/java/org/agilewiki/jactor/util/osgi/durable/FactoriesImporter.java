package org.agilewiki.jactor.util.osgi.durable;

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
public class FactoriesImporter extends ActorBase implements
        ServiceChangeReceiver<FactoryLocator> {

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
    private Transport<Void> startTransport;

    /**
     * The factory locator of the current bundle.
     */
    private final FactoryLocatorImpl factoryLocator;

    /**
     * Create and initialize a factories importer.
     *
     * @param _mailbox The mailbox of the factory locator actor.
     */
    public FactoriesImporter(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
        factoryLocator = (FactoryLocatorImpl) Durables
                .getFactoryLocator(_mailbox.getMailboxFactory());
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
    public Request<Void> startReq(final Filter _filter) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Void> _transport)
                    throws Exception {
                // We're got a start-request!
                // We only accept one start request.
                if (tracker != null)
                    throw new IllegalStateException("already started");
                // Create a service tracker for the given filter.
                tracker = new JAServiceTracker<FactoryLocator>(getMailbox(),
                        _filter);
                // Keep _transport for later, in case we do not find out service
                // at initial registration.
                startTransport = _transport;
                tracker.startReq(FactoriesImporter.this)
                        .send(getMailbox(),
                                // Damn you! Auto-formatter!
                                new ResponseProcessor<Map<ServiceReference, FactoryLocator>>() {
                                    @Override
                                    public void processResponse(
                                            final Map<ServiceReference, FactoryLocator> response)
                                            throws Exception {
                                        if (response.size() > 1) {
                                            tracker.close();
                                            tracker = null;
                                            startTransport = null;
                                            // We got too many services in the initial registration: Fail.
                                            // startTransport will get this exception as a response,
                                            // because the Mailbox will cascade it upward.
                                            throw new IllegalStateException(
                                                    "ambiguous filter--number of matches = "
                                                            + response.size());
                                        }
                                        if (response.size() == 1) {
                                            final FactoryLocator fl = response
                                                    .values().iterator().next();
                                            factoryLocator.importFactories(fl);
                                            // We got exactly one service in the initial registration
                                            // startTransport will get a response. :)
                                            startTransport = null;
                                            _transport.processResponse(null);
                                        }
                                        // We got zero services. Let's wait until
                                        // we get more service events. So we do
                                        // not answer startTransport yet ...
                                    }
                                });
            }
        };
    }

    /**
     * Got a service registration change. Probably either we finally get the
     * service we were waiting for, or we had it, and now it's gone.
     */
    @Override
    public void serviceChange(final ServiceEvent _event,
                              final Map<ServiceReference, FactoryLocator> _tracked,
                              final Transport _transport) throws Exception {
        _transport.processResponse(null);
        if (startTransport == null) {
            // If we get here, that means we had it, and now it's gone. :(
            tracker.close();
            tracker = null;
            log.error("Unexpected service change");
            factoryLocator.close();
            return;
        }
        if (_tracked.size() > 1) {
            // OK. Too many services. Fail and close tracker.
            tracker.close();
            tracker = null;
            startTransport
                    .processException(new IllegalStateException(
                            "ambiguous filter--number of matches = "
                                    + _tracked.size()));
            startTransport = null;
            return;
        }
        if (_tracked.size() == 1) {
            // Yeah! success!
            final FactoryLocator fl = _tracked.values().iterator().next();
            factoryLocator.importFactories(fl);
            startTransport.processResponse(null);
            startTransport = null;
            // But we keep tracking, in case it goes down later ...
            return;
        }
        // A serviceChange without any service? Despite the fact that we should
        // only come here when we did not find any services either at registration?
        log.info("strange case");
    }
}
