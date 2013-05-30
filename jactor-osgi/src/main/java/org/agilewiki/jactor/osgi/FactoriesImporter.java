package org.agilewiki.jactor.osgi;

import org.agilewiki.jactor.api.*;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Imports an OsgiFactoryLocator from another bundle into the factory locator of the current bundle.
 */
public class FactoriesImporter extends ActorBase implements
        ServiceChangeReceiver<OsgiFactoryLocator> {

    /**
     * Logger for this object.
     */
    private final Logger log = LoggerFactory.getLogger(FactoriesImporter.class);

    /**
     * The service tracker used to find and then monitor the set of matching services.
     */
    private JAServiceTracker<OsgiFactoryLocator> tracker;

    /**
     * The transport for the start request. Once a match is found,
     * startTransport is set to null.
     */
    private Transport<Void> startTransport;

    /**
     * The factory locator of the current bundle.
     */
    private final OsgiFactoryLocator factoryLocator;

    /**
     * Create and initialize a factories importer.
     *
     * @param _mailbox The mailbox of the factory locator actor.
     */
    public FactoriesImporter(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
        factoryLocator = Osgi.getOsgiFactoryLocator(_mailbox.getMailboxFactory());
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
                start(_filter, _transport);
            }
        };
    }

    private void start(final Filter _filter, final Transport<Void> _transport) throws Exception {
        // We're got a start-request!
        // We only accept one start request.
        if (tracker != null)
            throw new IllegalStateException("already started");
        // Create a service tracker for the given filter.
        tracker = new JAServiceTracker<OsgiFactoryLocator>(getMailbox(),
                _filter);
        // Keep _transport for later, in case we do not find out service
        // at initial registration.
        startTransport = _transport;
        tracker.startReq(FactoriesImporter.this)
                .signal(getMailbox());
    }

    public Request<Void> startReq(final String _bundleName, final String _niceVersion) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                start(_bundleName, _niceVersion, _transport);
            }
        };
    }

    private void start(final String _bundleName, final String _niceVersion, final Transport<Void> _transport)
            throws Exception {
        BundleContext bundleContext = Osgi.getBundleContext(getMailbox().getMailboxFactory());
        Filter filter = Osgi.factoryLocatorFilter(bundleContext, _bundleName, _niceVersion);
        start(filter, _transport);
    }

    public Request<Void> startReq(final String _bundleName, final Version _version) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                start(_bundleName, _version, _transport);
            }
        };
    }

    private void start(final String _bundleName, final Version _version, final Transport<Void> _transport)
            throws Exception {
        String niceVersion = Osgi.getNiceVersion(_version);
        start(_bundleName, niceVersion, _transport);
    }

    public Request<Void> startReq(final String _bundleLocation) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                start(_bundleLocation, _transport);
            }
        };
    }

    private void start(final String _bundleLocation, final Transport<Void> _transport)
            throws Exception {
        BundleContext bundleContext = Osgi.getBundleContext(getMailbox().getMailboxFactory());
        Bundle bundle = bundleContext.installBundle(_bundleLocation);
        bundle.start();
        String bundleName = bundle.getSymbolicName();
        Version version = bundle.getVersion();
        start(bundleName, version, _transport);
    }

    /**
     * Got a service registration change. Probably either we finally get the
     * service we were waiting for, or we had it, and now it's gone.
     */
    @Override
    public void serviceChange(final ServiceEvent _event,
                              final Map<ServiceReference, OsgiFactoryLocator> _tracked,
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
            final OsgiFactoryLocator fl = _tracked.values().iterator().next();
            factoryLocator.importFactoryLocator(fl);
            startTransport.processResponse(null);
            startTransport = null;
            // But we keep tracking, in case it goes down later ...
            return;
        }
        // A serviceChange without any service? Despite the fact that we should
        // only come here when we did not find any services either at registration?
        //log.info("strange case");
    }
}
