package org.agilewiki.jactor.kdriver;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.testIface.Hello;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.util.osgi.serviceTracker.JAServiceTracker;
import org.agilewiki.jactor.util.osgi.serviceTracker.ServiceChangeReceiver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;
import java.util.Map;

public class Activator extends MailboxFactoryActivator implements ServiceChangeReceiver<Hello> {
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
                JAServiceTracker<Hello> tracker = new JAServiceTracker(mailbox, Hello.class.getName());
               tracker.startReq(Activator.this).send(mailbox, new ResponseProcessor<Map<ServiceReference, Hello>>() {
                    @Override
                    public void processResponse(Map<ServiceReference, Hello> response) throws Exception {
                        processService(response);
                        _transport.processResponse(null);
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

    @Override
    public void serviceChange(ServiceEvent _event, Map<ServiceReference, Hello> _tracked, Transport _transport) throws Exception {
        _transport.processResponse(null);
        processService(_tracked);
    }

    private void processService(Map<ServiceReference, Hello> _tracked) throws Exception {
        if (_tracked.size() > 0) {
            success();
        }
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }
}
