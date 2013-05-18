package org.agilewiki.jactor.kdriver;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Request;
import org.agilewiki.jactor.api.RequestBase;
import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.osgi.framework.BundleContext;

public class Activator extends MailboxFactoryActivator {
    private Mailbox mailbox;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        super.start(_bundleContext);
        mailbox = getMailboxFactory().createMailbox();
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        dieReq().signal();
    }

    public Request<Void> dieReq() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                getMailboxFactory().close();
            }
        };
    }
}
