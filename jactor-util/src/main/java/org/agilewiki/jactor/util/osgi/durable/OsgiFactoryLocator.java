package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

/**
 * A factory locator that works with OSGi.
 */
public class OsgiFactoryLocator extends FactoryLocatorImpl {

    /**
     * The service registration.
     */
    private ServiceRegistration<FactoryLocator> serviceRegistration;

    /**
     * The mailbox factory to be closed when the factory locator is closed.
     */
    private MailboxFactory essentialService;

    /**
     * Mark the factory locator as essential.
     * When not null, closing the factory locator stops the bundle.
     * Otherwise, closing the factory locator just unregisters it.
     *
     * @param _mailboxFactory The mailbox factory of the bundle.
     */
    public void setEssentialService(final MailboxFactory _mailboxFactory) {
        essentialService = _mailboxFactory;
    }

    /**
     * Register the factory locator as an OSGi service.
     * Service reference attributes are bundleName and bundleVersion.
     *
     * @param _bundleContext The bundle context.
     */
    protected void register(final BundleContext _bundleContext) {
        Bundle bundle = _bundleContext.getBundle();
        configure(
                bundle.getSymbolicName(),
                MailboxFactoryActivator.niceVersion(bundle.getVersion()),
                bundle.getLocation());
        Hashtable<String, String> p = new Hashtable<String, String>();
        p.put("bundleName", getBundleName());
        p.put("bundleVersion", getNiceVersion());
        serviceRegistration = _bundleContext.registerService(FactoryLocator.class, this, p);
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (essentialService != null) {
            essentialService.close();
        } else
            serviceRegistration.unregister();
    }
}
