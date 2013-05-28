package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.osgi.framework.BundleContext;

/**
 * A basic activator with a registered factory locator service but no mailbox factory.
 */
abstract public class FactoryLocatorActivator0 extends MailboxFactoryActivator {

    /**
     * The factory locator service.
     */
    protected OsgiFactoryLocator factoryLocator;

    protected boolean configImports() {
        return true;
    }

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        initializeActivator(_bundleContext);
        createFactoryLocator();
        if (!configImports())
            factoryLocator.register(bundleContext);
        managedServiceRegistration();
    }

    @Override
    protected void configInitialized() {
        if (configImports())
            factoryLocator.register(bundleContext);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
    }

    /**
     * Returns the factory locator of the bundle.
     *
     * @return The factory locator.
     */
    protected final FactoryLocator getFactoryLocator() {
        return factoryLocator;
    }

    /**
     * Create and initialize the factory locator.
     */
    protected void createFactoryLocator() throws Exception {
        factoryLocator = new OsgiFactoryLocator();
    }
}
