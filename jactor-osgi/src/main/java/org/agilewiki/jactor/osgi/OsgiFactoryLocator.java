package org.agilewiki.jactor.osgi;

import org.agilewiki.jactor.api.MailboxFactory;
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

    /**
     * The mailbox factory to which the factory locator is bound.
     */
    private MailboxFactory mailboxFactory;

    /**
     * The contents of the bundle's config file.
     */
    private Dictionary<String, ?> properties;

    /**
     * The service registration returned when this service was registered.
     */
    private ServiceRegistration<OsgiFactoryLocator> serviceRegistration;

    /**
     * True when the mailbox factory is to be closed when the factory locator is closed.
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

    /**
     * Returns the mailbox factory to which this factory locator is bound.
     *
     * @return The mailbox factory.
     */
    public MailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    /**
     * Bind this factory locator to a mailbox factory.
     *
     * @param _mailboxFactory    The mailbox factory.
     */
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
