package org.agilewiki.jactor.testService;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.testIface.Hello;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.util.osgi.durable.FactoriesImporter;
import org.agilewiki.jactor.util.osgi.durable.FactoryLocatorActivator;
import org.osgi.framework.*;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator extends FactoryLocatorActivator {
    private final Logger logger = LoggerFactory.getLogger(Activator.class);
    private Mailbox mailbox;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        super.start(_bundleContext);
        factoryLocator.setEssentialService(getMailboxFactory());
        mailbox = getMailboxFactory().createMailbox();
        HelloService hello = new HelloService(_bundleContext, mailbox);
        ServiceRegistration hsr = _bundleContext.registerService(
                Hello.class.getName(),
                hello,
                new Hashtable<String, String>());
        Version version = bundleContext.getBundle().getVersion();
        Hashtable<String, String> mp = new Hashtable<String, String>();
        mp.put(Constants.SERVICE_PID, "org.agilewiki.jactor.testService." + version.toString());
        ServiceRegistration msr = _bundleContext.registerService(
                ManagedService.class.getName(),
                hello,
                mp);
    }

    @Override
    protected void createFactoryLocator() throws Exception {
        super.createFactoryLocator();
        Mailbox mailbox = getMailboxFactory().createMailbox();
        FactoriesImporter factoriesImporter = new FactoriesImporter(mailbox);
        String fs = "(&" +
                "(objectClass=org.agilewiki.jactor.util.durable.FactoryLocator)" +
                "(&(bundleName=jactor-util)(bundleVersion=0.0.1-SNAPSHOT))" +
                ")";
        Filter filter = bundleContext.createFilter(fs);
        factoriesImporter.startReq(filter).call();
    }
}
