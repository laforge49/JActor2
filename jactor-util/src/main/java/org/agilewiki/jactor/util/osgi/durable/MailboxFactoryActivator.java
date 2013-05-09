package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.api.Properties;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;
import org.agilewiki.jactor.util.JAProperties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class MailboxFactoryActivator implements BundleActivator, AutoCloseable {
    public static BundleContext getBundleContext(final MailboxFactory _mailboxFactory) {
        Properties p = _mailboxFactory.getProperties();
        return (BundleContext) p.getProperty("bundleContext");
    }

    private MailboxFactory mailboxFactory;
    private JAProperties jaProperties;
    protected BundleContext bundleContext;
    private boolean closing;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        setBundleContext(_bundleContext);
        mailboxFactoryStart();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        MailboxFactoryStop();
    }

    protected final void setBundleContext(final BundleContext _bundleContext) {
        bundleContext = _bundleContext;
    }

    protected MailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    protected final void mailboxFactoryStart() throws Exception {
        mailboxFactory = new DefaultMailboxFactoryImpl();
        mailboxFactory.addAutoClosable(this);
        jaProperties = new JAProperties(mailboxFactory, null);
        jaProperties.putProperty("bundleContext", bundleContext);
    }

    protected final void MailboxFactoryStop() throws Exception {
        closing = true;
        mailboxFactory.close();
    }

    protected final boolean isBundleClosing() {
        return closing;
    }

    protected final void setClosing() {
        closing = true;
    }

    @Override
    public void close() throws Exception {
        if (closing)
            return;
        Bundle bundle = bundleContext.getBundle();
        bundle.stop(Bundle.STOP_TRANSIENT);
    }
}
