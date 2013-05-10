package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.Properties;
import org.osgi.framework.BundleContext;

public class FactoryLocatorActivator extends FactoryLocatorActivator0 {

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        setBundleContext(_bundleContext);
        mailboxFactoryStart();
        factoryLocatorStart();
    }

    protected void createFactoryLocator() throws Exception {
        super.createFactoryLocator();
        Properties properties = getMailboxFactory().getProperties();
        properties.putProperty("factoryLocator", factoryLocator);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        MailboxFactoryStop();
    }
}
