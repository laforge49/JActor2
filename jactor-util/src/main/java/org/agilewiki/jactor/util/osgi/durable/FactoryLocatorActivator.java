package org.agilewiki.jactor.util.osgi.durable;

import org.osgi.framework.BundleContext;

public class FactoryLocatorActivator extends FactoryLocatorActivator0 {

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        setBundleContext(_bundleContext);
        factoryLocatorStart();
        mailboxFactoryStart();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        MailboxFactoryStop();
    }
}
