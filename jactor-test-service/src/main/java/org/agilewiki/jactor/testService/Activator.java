package org.agilewiki.jactor.testService;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.testIface.Hello;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator extends MailboxFactoryActivator {
    private final Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        logger.error("testUtil location: " + bundleContext.getBundle().getLocation());
        logger.error("testUtil location: " + bundleContext.getBundle().getSymbolicName());
        Mailbox mailbox = getMailboxFactory().createMailbox();
        HelloService hello = new HelloService(context, mailbox);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        ServiceRegistration sr = context.registerService(
                Hello.class.getName(),
                hello,
                new Hashtable<String, String>());
    }
}
