package org.agilewiki.jactor.kdriver;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.testIface.Hello;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.util.osgi.serviceTracker.JAServiceTracker;
import org.agilewiki.jactor.util.osgi.serviceTracker.LocateService;
import org.agilewiki.jactor.util.osgi.serviceTracker.ServiceChangeReceiver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;
import java.util.Map;

public class Activator extends MailboxFactoryActivator {
    private Mailbox mailbox;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        super.start(_bundleContext);
        mailbox = getMailboxFactory().createMailbox();
        startReq().signal();
    }

    public Request<Void> startReq() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(final Transport<Void> _transport) throws Exception {
                LocateService<Hello> locateService = new LocateService(mailbox, Hello.class.getName());
               locateService.getReq().send(mailbox, new ResponseProcessor<Hello>() {
                   @Override
                   public void processResponse(Hello response) throws Exception {
                       _transport.processResponse(null);
                       success();
                   }
               });
            }
        };
    }

    public void success() {
        Hashtable<String, String> p = new Hashtable<String, String>();
        p.put("kdriverSuccess", "true");
        bundleContext.registerService(
                KDriverSuccess.class.getName(),
                new KDriverSuccess(),
                p);
    }
}
