package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.Properties;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

/**
 * A basic activator with a registered factory locator service but no mailbox.
 */
public class FactoryLocatorActivator0 extends MailboxFactoryActivator {

    /**
     * The factory locator service.
     */
    protected OsgiFactoryLocator factoryLocator;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        setBundleContext(_bundleContext);
        factoryLocatorStart();
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
     * Create, initialize and register the factory locator.
     */
    protected final void factoryLocatorStart() throws Exception {
        createFactoryLocator();
        factoryLocator.register(bundleContext);
    }

    /**
     * Create and initialize the factory locator.
     */
    protected void createFactoryLocator() throws Exception {
        factoryLocator = new OsgiFactoryLocator();
    }
}
