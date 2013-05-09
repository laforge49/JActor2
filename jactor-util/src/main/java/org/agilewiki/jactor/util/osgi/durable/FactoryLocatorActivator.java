package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public class FactoryLocatorActivator extends MailboxFactoryActivator {
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

    protected final FactoryLocator getFactoryLocator() {
        return factoryLocator;
    }

    protected final void factoryLocatorStart() throws Exception {
        createFactoryLocator();
        factoryLocator.register(bundleContext);
    }

    protected void createFactoryLocator() throws Exception {
        factoryLocator = new OsgiFactoryLocator();
    }
}
