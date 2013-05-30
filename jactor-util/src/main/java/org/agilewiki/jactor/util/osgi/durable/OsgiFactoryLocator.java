package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.util.osgi.Osgi;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * A factory locator that works with OSGi.
 */
public class OsgiFactoryLocator extends FactoryLocatorImpl implements ManagedService {

    private MailboxFactory mailboxFactory;

    private Dictionary<String, ?> properties;
    /**
     * The service registration.
     */
    private ServiceRegistration<OsgiFactoryLocator> serviceRegistration;

    /**
     * The mailbox factory to be closed when the factory locator is closed.
     */
    private boolean essentialService;

    /**
     * Mark the factory locator as essential.
     * When not null, closing the factory locator stops the bundle.
     * Otherwise, closing the factory locator just unregisters it.
     */
    public void setEssentialService() {
        essentialService = true;
    }

    public MailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    public void setMailboxFactory(final MailboxFactory _mailboxFactory) {
        if (mailboxFactory != null)
            throw new IllegalStateException("mailbox factory already set");
        mailboxFactory = _mailboxFactory;
    }

    /**
     * Register the factory locator as an OSGi service.
     * Service reference attributes are bundleName and bundleVersion.
     *
     * @param _bundleContext The bundle context.
     */
    public void register(final BundleContext _bundleContext) {
        Bundle bundle = _bundleContext.getBundle();
        configure(
                bundle.getSymbolicName(),
                Osgi.getNiceVersion(bundle.getVersion()),
                bundle.getLocation());
        Hashtable<String, String> p = new Hashtable<String, String>();
        p.put("bundleName", getBundleName());
        p.put("bundleVersion", getNiceVersion());
        serviceRegistration = _bundleContext.registerService(OsgiFactoryLocator.class, this, p);
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (essentialService) {
            mailboxFactory.close();
        } else if (serviceRegistration != null)
            serviceRegistration.unregister();
    }

    @Override
    public void updated(final Dictionary<String, ?> _properties) throws ConfigurationException {
        if (_properties != null)
            throw new IllegalStateException("bundle restart required");
    }
}
