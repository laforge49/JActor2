package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.Properties;
import org.agilewiki.jactor.api.Transport;
import org.osgi.framework.BundleContext;

/**
 * An activator that provides both a mailbox factory and a registered factory locator service.
 */
abstract public class FactoryLocatorActivator extends FactoryLocatorActivator0 {

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        initializeActivator(_bundleContext);
        mailboxFactoryStart();
        createFactoryLocator();
        beginReq().signal();
    }

    @Override
    protected void begin(final Transport<Void> _transport) throws Exception {
        factoryLocator.register(bundleContext);
        managedServiceRegistration();
        _transport.processResponse(null);
    }

    protected void createFactoryLocator() throws Exception {
        super.createFactoryLocator();
        Properties properties = getMailboxFactory().getProperties();
        properties.putProperty("factoryLocator", factoryLocator);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        getMailboxFactory().close();
    }
}
