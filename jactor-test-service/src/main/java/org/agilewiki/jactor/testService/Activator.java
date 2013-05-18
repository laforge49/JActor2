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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator extends MailboxFactoryActivator {
    private final Logger logger = LoggerFactory.getLogger(Activator.class);
    private Mailbox mailbox;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        super.start(_bundleContext);
//        factoryLocator.setEssentialService(getMailboxFactory());
        mailbox = getMailboxFactory().createMailbox();
        HelloService hello = new HelloService(_bundleContext, mailbox);
        ServiceRegistration sr = _bundleContext.registerService(
                Hello.class.getName(),
                hello,
                new Hashtable<String, String>());
    }

    /*
    @Override
    protected void createFactoryLocator() throws Exception {
        super.createFactoryLocator();
        Mailbox mailbox = getMailboxFactory().createMailbox();
        FactoriesImporter factoriesImporter = new FactoriesImporter(mailbox);
        String fs = "(&(objectClass=org.agilewiki.jactor.util.durable.FactoryLocator)(&(bundleName=jactor-util)(bundleVersion=0.0.1.SNAPSHOT)))";
        Filter filter = bundleContext.createFilter(fs);
        factoriesImporter.startReq(filter).call();
    }
    */
}
