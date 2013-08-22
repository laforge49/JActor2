package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
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
     * The processing factory to which the factory locator is bound.
     */
    private ModuleContext moduleContext;

    /**
     * The contents of the bundle's config file.
     */
    private Dictionary<String, ?> properties;

    /**
     * The service registration returned when this service was registered.
     */
    private ServiceRegistration<OsgiFactoryLocator> serviceRegistration;

    /**
     * True when the processing factory is to be closed when the factory locator is closed.
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
     * Returns the module context to which this factory locator is bound.
     *
     * @return The processing factory.
     */
    public ModuleContext getModuleContext() {
        return moduleContext;
    }

    /**
     * Bind this factory locator to a module context.
     *
     * @param _moduleContext The processing factory.
     */
    public void setModuleContext(final ModuleContext _moduleContext) {
        if (moduleContext != null)
            throw new IllegalStateException("processing factory already set");
        moduleContext = _moduleContext;
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
            moduleContext.close();
        } else if (serviceRegistration != null)
            serviceRegistration.unregister();
    }

    @Override
    public void updated(final Dictionary<String, ?> _properties) throws ConfigurationException {
        if (_properties != null)
            throw new IllegalStateException("bundle restart required");
    }
}
