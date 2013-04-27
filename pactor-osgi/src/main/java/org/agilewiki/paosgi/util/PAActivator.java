package org.agilewiki.paosgi.util;

import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;
import org.agilewiki.pautil.PAProperties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PAActivator implements BundleActivator, AutoCloseable {
    private MailboxFactory mailboxFactory;
    private PAProperties paProperties;
    protected BundleContext bundleContext;
    private boolean closing;

    protected MailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    protected void createMailboxFactory() throws Exception {
        mailboxFactory = new DefaultMailboxFactoryImpl();
        mailboxFactory.addAutoClosable(this);
        paProperties = new PAProperties(mailboxFactory, null);
        paProperties.putProperty("bundleContext", bundleContext);
    }

    protected boolean isBundleClosing() {
        return closing;
    }

    @Override
    public void start(final BundleContext _context) throws Exception {
        bundleContext = _context;
        createMailboxFactory();
    }

    @Override
    public final void stop(BundleContext context) throws Exception {
        closing = true;
        mailboxFactory.close();
    }

    @Override
    public void close() throws Exception {
        if (closing)
            return;
        Bundle bundle = bundleContext.getBundle();
        bundle.stop(Bundle.STOP_TRANSIENT);
    }
}
