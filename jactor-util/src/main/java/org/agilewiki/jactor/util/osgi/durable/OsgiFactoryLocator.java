package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.utilImpl.durable.FactoryLocatorImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class OsgiFactoryLocator extends FactoryLocatorImpl {

    private ServiceRegistration<FactoryLocator> serviceRegistration;

    private MailboxFactory essentialService;

    public void setEssentialService(final MailboxFactory _mailboxFactory) {
        essentialService = _mailboxFactory;
    }

    protected void register(final BundleContext _bundleContext) {
        Bundle bundle = _bundleContext.getBundle();
        configure(
                bundle.getSymbolicName(),
                bundle.getVersion().toString(),
                bundle.getLocation());
        Hashtable<String, String> p = new Hashtable<String, String>();
        p.put("bundleName", getBundleName());
        p.put("bundleVersion", getVersion());
        System.out.println("xxxxxxxxxxxxxxxxxxxxx");
        System.out.println(p);
        System.out.println("xxxxxxxxxxxxxxxxxxxxx");
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
