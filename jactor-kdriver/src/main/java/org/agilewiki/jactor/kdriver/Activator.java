package org.agilewiki.jactor.kdriver;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class Activator extends MailboxFactoryActivator {
    private Mailbox mailbox;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        super.start(_bundleContext);
        mailbox = getMailboxFactory().createMailbox();
        Hashtable<String, String> p = new Hashtable<String, String>();
        p.put("kdriverSuccess", "true");
        ServiceRegistration sr = _bundleContext.registerService(
                KDriverSuccess.class.getName(),
                new KDriverSuccess(),
                p);
        dieReq().signal();
    }

    public Request<Void> dieReq() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                Thread.sleep(10000);
                getMailboxFactory().close();
            }
        };
    }
}
