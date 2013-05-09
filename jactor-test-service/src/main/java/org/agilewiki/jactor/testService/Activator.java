package org.agilewiki.jactor.testService;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.testIface.Hello;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.util.osgi.durable.FactoriesImporter;
import org.agilewiki.jactor.util.osgi.durable.FactoryLocatorActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator extends FactoryLocatorActivator {
    private final Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        Mailbox mailbox = getMailboxFactory().createMailbox();
        FactoriesImporter factoriesImporter = new FactoriesImporter(mailbox);
        String fs = "(&(objectClass=org.agilewiki.jactor.util.durable.FactoryLocator)(&(bundleName=jactor-util)(bundleVersion=0.0.1.SNAPSHOT)))";
        Filter filter = context.createFilter(fs);
        factoriesImporter.startReq(filter).call();


        logger.error("testUtil location: " + bundleContext.getBundle().getLocation());
        logger.error("testUtil location: " + bundleContext.getBundle().getSymbolicName());
        HelloService hello = new HelloService(context, mailbox);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        ServiceRegistration sr = context.registerService(
                Hello.class.getName(),
                hello,
                new Hashtable<String, String>());
    }
}
